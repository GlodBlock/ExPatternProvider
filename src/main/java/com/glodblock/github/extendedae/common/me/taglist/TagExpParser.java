package com.glodblock.github.extendedae.common.me.taglist;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author brachy84
 */
public final class TagExpParser {

    private final static ReferenceSet<TagKey<?>> TAGS = new ReferenceOpenHashSet<>();
    private static boolean isInit = false;
    private final static LoadingCache<String, Set<TagKey<?>>> CACHE = CacheBuilder.newBuilder().build(
            new CacheLoader<>() {
                @Override
                public @NotNull Set<TagKey<?>> load(@NotNull String key) {
                    return getMatchingOreInternal(key);
                }
            }
    );

    public static void reset() {
        isInit = false;
        CACHE.invalidateAll();
    }

    private static void init() {
        TAGS.clear();
        TAGS.addAll(ForgeRegistries.ITEMS.tags().getTagNames().toList());
        TAGS.addAll(ForgeRegistries.FLUIDS.tags().getTagNames().toList());
    }

    public static Set<TagKey<?>> getMatchingOre(String oreExp) {
        oreExp = validateExp(oreExp);
        return CACHE.getUnchecked(oreExp);
    }

    private static Set<TagKey<?>> getMatchingOreInternal(String oreExp) {
        if (oreExp.isEmpty()) {
            return Set.of();
        }
        if (!isInit) {
            init();
            isInit = true;
        }
        Set<TagKey<?>> matchingIds = new HashSet<>();
        List<MatchRule> rulesList = parseExpression(oreExp);
        if (rulesList.isEmpty()) {
            return Set.of();
        }
        for (var tag : TAGS) {
            if (matches(rulesList, tag.location().toString())) {
                matchingIds.add(tag);
            }
        }
        return matchingIds;
    }

    public static List<MatchRule> parseExpression(String expression) {
        List<MatchRule> rules = new ArrayList<>();
        parseExpression(rules, expression);
        return rules;
    }

    public static int parseExpression(List<MatchRule> rules, String expression) {
        rules.clear();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == ' ') {
                continue;
            }
            if (c == '(') {
                List<MatchRule> subRules = new ArrayList<>();
                i = parseExpression(subRules, expression.substring(i + 1)) + i + 1;
                rules.add(MatchRule.group(subRules));
                builder = new StringBuilder();
            } else {
                switch (c) {
                    case '&' -> {
                        rules.add(new MatchRule(builder.toString()));
                        rules.add(new MatchRule(MatchLogic.AND));
                        builder = new StringBuilder();
                    }
                    case '|' -> {
                        rules.add(new MatchRule(builder.toString()));
                        rules.add(new MatchRule(MatchLogic.OR));
                        builder = new StringBuilder();
                    }
                    case '^' -> {
                        rules.add(new MatchRule(builder.toString()));
                        rules.add(new MatchRule(MatchLogic.XOR));
                        builder = new StringBuilder();
                    }
                    case ')' -> {
                        rules.add(new MatchRule(builder.toString()));
                        return i + 1;
                    }
                    default -> builder.append(c);
                }
            }
        }
        if (!builder.isEmpty()) {
            rules.add(new MatchRule(builder.toString()));
        }
        return expression.length();
    }

    public static boolean matches(List<MatchRule> rules, String oreDict) {
        boolean first = true;
        boolean lastResult = false;
        MatchLogic lastLogic = null;
        for (MatchRule rule : rules) {
            if (lastLogic == null) {
                if (rule.logic == MatchLogic.AND || rule.logic == MatchLogic.OR || rule.logic == MatchLogic.XOR) {
                    lastLogic = rule.logic;
                    continue;
                }
            }
            if (lastLogic != null || first) {
                if (lastLogic != null) {
                    switch (lastLogic) {
                        case AND -> {
                            if (!lastResult) {
                                return false;
                            }
                        }
                        case OR -> {
                            if (lastResult) {
                                return true;
                            }
                        }
                    }
                }

                boolean newResult;
                if (rule.isGroup()) {
                    newResult = matches(rule.subRules, oreDict);
                } else {
                    newResult = matches(rule, oreDict);
                }

                if (lastLogic == MatchLogic.XOR) {
                    if (lastResult == newResult) {
                        return false;
                    }
                }

                lastLogic = null;
                lastResult = newResult;
                first = false;
            }

        }

        return lastResult;
    }

    private static boolean matches(MatchRule rule, String oreDict) {
        String filter = rule.expression;

        if (filter.equals("*")) {
            return true;
        }

        boolean startWild = filter.startsWith("*"), endWild = filter.endsWith("*");
        if (startWild) {
            filter = filter.substring(1);
        }

        String[] parts = filter.split("\\*+");

        return matches(parts, oreDict, startWild, endWild);
    }

    private static boolean matches(String[] filter, String oreDict, boolean startWild, boolean endWild) {
        String lastlastPart = filter[0];
        String lastPart = filter[0];
        int index = oreDict.indexOf(lastPart);
        if ((!startWild && index != 0) || index < 0) {
            return false;
        }
        boolean didGoBack = false;

        for (int i = 1; i < filter.length; i++) {
            String part = filter[i];
            int newIndex = oreDict.indexOf(part, index + lastPart.length());
            if (newIndex < 0) {
                if (i > 1 && !didGoBack) {
                    i -= 2;
                    lastPart = lastlastPart;
                    didGoBack = true;
                    continue;
                }
                return false;
            }
            lastlastPart = lastPart;
            lastPart = part;
            index = newIndex;
            if (didGoBack) {
                didGoBack = false;
            }
        }

        if (endWild || lastPart.length() + index == oreDict.length()) {
            return true;
        }

        for (int i = filter.length - 1; i < filter.length; i++) {
            String part = filter[i];
            int newIndex = oreDict.indexOf(part, index + lastPart.length());
            if (newIndex < 0) {
                if (i > 1 && !didGoBack) {
                    i -= 2;
                    lastPart = lastlastPart;
                    didGoBack = true;
                    continue;
                }
                return false;
            }
            lastlastPart = lastPart;
            lastPart = part;
            index = newIndex;
            if (didGoBack) {
                didGoBack = false;
            }
        }
        return lastPart.length() + index == oreDict.length();
    }

    private static String validateExp(String input) {
        // remove all operators that are double
        input = input.replaceAll("\\*{2,}", "*");
        input = input.replaceAll("&{2,}", "&");
        input = input.replaceAll("\\|{2,}", "|");
        input = input.replaceAll("\\^{2,}", "^");
        input = input.replaceAll(" {2,}", " ");
        // move ( and ) so it doesn't create invalid expressions f.e. xxx (& yyy) => xxx & (yyy)
        // append or prepend ( and ) if the amount is not equal
        StringBuilder builder = new StringBuilder();
        int unclosed = 0;
        char last = ' ';
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == ' ') {
                if (last != '(') {
                    builder.append(" ");
                }
                continue;
            }
            if (c == '(') {
                unclosed++;
            } else if (c == ')') {
                unclosed--;
                if (last == '&' || last == '|' || last == '^') {
                    int l = builder.lastIndexOf(" " + last);
                    int l2 = builder.lastIndexOf("" + last);
                    builder.insert(l == l2 - 1 ? l : l2, ")");
                    continue;
                }
                if (i > 0 && builder.charAt(builder.length() - 1) == ' ') {
                    builder.deleteCharAt(builder.length() - 1);
                }
            } else if ((c == '&' || c == '|' || c == '^') && last == '(') {
                builder.deleteCharAt(builder.lastIndexOf("("));
                builder.append(c).append(" (");
                continue;
            }

            builder.append(c);
            last = c;
        }
        if (unclosed > 0) {
            builder.append(")".repeat(unclosed));
        } else if (unclosed < 0) {
            unclosed = -unclosed;
            for (int i = 0; i < unclosed; i++) {
                builder.insert(0, "(");
            }
        }
        input = builder.toString();
        input = input.replaceAll(" {2,}", " ");
        return input;
    }


    public static class MatchRule {
        public final MatchLogic logic;
        public final String expression;
        private final List<MatchRule> subRules;

        private MatchRule(MatchLogic logic, String expression, List<MatchRule> subRules) {
            this.logic = logic;
            this.expression = expression;
            this.subRules = subRules;
        }

        public MatchRule(MatchLogic logic, String expression) {
            this(logic, expression, null);
        }

        public MatchRule(MatchLogic logic) {
            this(logic, "");
        }

        public MatchRule(String expression) {
            this(MatchLogic.ANY, expression);
        }

        public static MatchRule group(List<MatchRule> subRules) {
            return new MatchRule(MatchLogic.ANY, "", subRules);
        }

        public boolean isGroup() {
            return subRules != null;
        }
    }


    public enum MatchLogic {
        OR,
        AND,
        XOR,
        ANY
    }

}
