package org.forkjoin.scrat.apikit.tool.utils;

import org.apache.commons.lang3.StringUtils;
import org.forkjoin.scrat.apikit.tool.info.JavadocInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zuoge85@gmail.com on 2016/11/23.
 */
public class CommentUtils {


    /**
     * 不包含tag 的内容
     */
    public static String formatCommentNoTag(JavadocInfo comment, String start) {
        if (comment == null) {
            return start;
        }
        StringBuilder sb = new StringBuilder();

        for (String tagName : comment.getTags().keySet()) {
            if (StringUtils.isEmpty(tagName)) {
                sb.append(start);
                formatCommentItem(comment, start, sb, tagName);
            }
        }
        if (sb.length() > 0) {
            return sb.toString();
        } else {
            return start;
        }
    }

    /**
     * 不带tabName 的内容
     */
    public static String formatBaseComment(JavadocInfo comment, String start) {
        if (comment == null) {
            return start;
        }
        StringBuilder sb = new StringBuilder();

        List<List<String>> lists = comment.get(null);
        lists.stream().flatMap(Collection::stream).forEach(c -> {
            sb.append(start);
            sb.append(StringUtils.trimToEmpty(c).replace("\n", "").replace("\r", ""));
            sb.append("\n");
        });
        String str = StringUtils.stripEnd(sb.toString(), null);
        if (str.length() > 0) {
            return str;
        } else {
            return start;
        }
    }

    private static void formatCommentItem(JavadocInfo comment, String start, StringBuilder sb, String tagName) {
        List<List<String>> fragments = comment.getTags().get(tagName);
        sb.append(fragments.stream().flatMap(Collection::stream).collect(Collectors.joining("\n" + start)));
    }

    public static String formatComment(JavadocInfo comment, String start) {
        if (comment == null) {
            return start;
        }
        StringBuilder sb = new StringBuilder();
        for (String tagName : comment.getTags().keySet()) {
            sb.append(start);
            if (StringUtils.isNotEmpty(tagName)) {
                sb.append(tagName);
                sb.append(' ');
            }
            formatCommentItem(comment, start, sb, tagName);
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }


    public static Map<String, String> commentToMap(JavadocInfo comment) {
        if (comment == null) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();

        for (String tagName : comment.getTags().keySet()) {
            StringBuilder sb = new StringBuilder();

            boolean isParam = "@param".equals(tagName);

            Collection<List<String>> fragments = comment.getTags().get(tagName);

            if (isParam) {
                fragments.forEach(fragment -> {
                    if (!fragment.isEmpty()) {
                        String paramName = fragment.get(0);
                        map.put(paramName, fragments.stream().skip(1).flatMap(Collection::stream).collect(Collectors.joining(" ")));
                    }
                });
            } else {
                map.put(tagName, fragments.stream().flatMap(Collection::stream).collect(Collectors.joining(" ")));
            }
        }
        return map;
    }
}
