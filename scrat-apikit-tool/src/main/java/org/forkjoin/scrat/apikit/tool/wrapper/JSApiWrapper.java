package org.forkjoin.scrat.apikit.tool.wrapper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.forkjoin.scrat.apikit.tool.Context;
import org.forkjoin.scrat.apikit.tool.info.ApiInfo;
import org.forkjoin.scrat.apikit.tool.info.ApiMethodInfo;
import org.forkjoin.scrat.apikit.tool.info.ApiMethodParamInfo;
import org.forkjoin.scrat.apikit.tool.info.TypeInfo;
import org.forkjoin.scrat.apikit.tool.utils.CommentUtils;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author zuoge85 on 15/6/14.
 */
public class JSApiWrapper extends JSWrapper<ApiInfo> {
    private String version;
    private String jsPackageName;

    public JSApiWrapper(Context context, ApiInfo moduleInfo, String rootPackage, String jsPackageName) {
        super(context, moduleInfo, rootPackage);
        this.jsPackageName = jsPackageName;
    }

    public String getImports() {
        return getImports(true);
    }

    //
    public String getImports(boolean isModel) {
        //自己的目录级别
        int myLevel = getMyLevel();
        String imports = isModel ? getMethodImports() : "";
        return imports +
                "\nimport {AbstractApi} from 'apikit-core'\n" +
                "\nimport {requestGroupImpi} from 'apikit-core'\n";

    }


//    public String getEs5Imports() {
//        //自己的目录级别
//        int myLevel = getMyLevel();
//        String str = StringUtils.repeat("../", myLevel);
//
//        return "var _AbstractApi2 = require(\"" + str + "AbstractApi\");\n" +
//                "var _AbstractApi3 = _interopRequireDefault(_AbstractApi2);\n" +
//                "\n" +
//                "var _RequestGroupImpi = require(\"" + str + "RequestGroupImpi\");\n" +
//                "var _RequestGroupImpi2 = _interopRequireDefault(_RequestGroupImpi);\n";
//    }


    private int getMyLevel() {
        String moduleInfoPackageName = getDistPackage();
        if (StringUtils.isEmpty(moduleInfoPackageName)) {
            return 0;
        } else {
            return moduleInfoPackageName.split(".").length + 1;
        }
    }

    public String getMethodImports() {
        StringBuilder sb = new StringBuilder();

        int myLevel = getMyLevel();


        Flux
                .fromIterable(moduleInfo.getMethodInfos())
                .flatMapIterable(m -> {
                    List<TypeInfo> types = new ArrayList<>();
                    types.add(m.getResultDataType());
                    m.getParams().forEach(p -> types.add(p.getTypeInfo()));
                    return types;
                })
                .flatMapIterable(type -> {
                    List<TypeInfo> types = new ArrayList<>();
                    findTypes(type, types);
                    return types;
                })
                .filter(typeInfo -> typeInfo.getType().equals(TypeInfo.Type.OTHER))
                .filter(typeInfo -> !typeInfo.isCollection())
                .filter(typeInfo -> !typeInfo.isGeneric())
                .map(TypeInfo::getFullName)
                .distinct()
                .sort(Comparator.naturalOrder())
                .filter(fullName -> context.getMessageWrapper(fullName) != null)
                .map(fullName -> context.getMessageWrapper(fullName))
                .filter(w -> !w.getDistPackage().equals(getDistPackage()))
                .doOnNext(r -> {
                    String name = r.getDistName();
                    sb.append("import ")
                            .append(name)
                            .append(" from './")
                            .append(StringUtils.repeat("../", myLevel))
                            .append(r.getDistPackage().replace(".", "/")).append('/').append(name).append("'\n").toString();
                })
                .collectList()
                .block();

        return sb.toString();
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String result(ApiMethodInfo method) {
        StringBuilder sb = new StringBuilder();
        TypeInfo resultType = method.getResultType();
        sb.append(toTypeString(resultType));
        return sb.toString();
    }

    public String resultClass(ApiMethodInfo method) {
        StringBuilder sb = new StringBuilder();
        TypeInfo resultType = method.getResultType();
        sb.append(toTypeString(resultType));
        sb.append(".class");
        return sb.toString();
    }

    /**
     *
     */
    public String requestComment(ApiMethodInfo method, String start) {
        StringBuilder sb = new StringBuilder(start);
        sb.append("<div class='http-info'>http 说明：<b>Api Url:</b> <b>")
                .append(method.getUrl())
                .append("</b>\n")
                .append(start)
                .append("<ul>\n");

//        sb.append(start).append("<li><b>Method:</b> ")
//                .append(method.getType().toMethod())
//                .append("</li>\n");
        Map<String, String> stringStringMap = CommentUtils.commentToMap(method.getComment());

        ArrayList<ApiMethodParamInfo> params = method.getParams();
        for (ApiMethodParamInfo attributeInfo : params) {
            if (attributeInfo.isPathVariable()) {
                String name = attributeInfo.getName();
                String txt = stringStringMap.get(name);
                sb.append(start).append("<li><b>PathVariable:</b> ")
                        .append(
                                StringEscapeUtils.escapeHtml4(
                                        toTypeString(attributeInfo.getTypeInfo())
                                )
                        )
                        .append(" ")
                        .append(attributeInfo.getName());

                if (StringUtils.isNotEmpty(txt)) {
                    sb.append(" ");
                    sb.append("<span>");
                    sb.append(txt);
                    sb.append("</span>");
                }
                sb.append("</li>\n");
            } else if (attributeInfo.isFormParam()) {
                sb.append(start).append("<li><b>Form:</b>")
                        .append(
                                StringEscapeUtils.escapeHtml4(
                                        toTypeString(attributeInfo.getTypeInfo())
                                )
                        )
                        .append("")
                        .append(method.getName())
                        .append("</li>\n");
            }
        }

        String returnType = toTypeString(method.getResultType());

        sb.append(start).append("<li><b>Model:</b> ").append("").append(
                StringEscapeUtils.escapeHtml4(returnType)
        ).append("")
                .append("</li>\n");

        if (method.isAccount()) {
            sb.append(start).append("<li>需要登录</li>\n");
        }


        sb.append(start).append("</ul>\n").append(start).append("</div>\n");

        for (ApiMethodParamInfo attributeInfo : method.getParams()) {
            if (attributeInfo.isPathVariable() || attributeInfo.isFormParam()) {
                String name = attributeInfo.getName();
                String txt = stringStringMap.get(name);
                if (StringUtils.isNotEmpty(txt)) {
                    sb.append(start).append("@param ")
                            .append(name)
                            .append(" ")
                            .append(txt.replace("\n", ""))
                            .append("\n");
                }
            }
        }

        if (method.getResultType().getType() != TypeInfo.Type.VOID) {
            sb.append(start).append("@see ").append(
                    StringEscapeUtils.escapeHtml4(
                            toTypeString(method.getResultType())
                    )
            ).append("\n");
        }

        for (ApiMethodParamInfo attributeInfo : method.getParams()) {
            if ((attributeInfo.isPathVariable() || attributeInfo.isFormParam()) && attributeInfo.getTypeInfo().getType() != TypeInfo.Type.VOID) {
                sb.append(start).append("@see ").append(
                        StringEscapeUtils.escapeHtml4(toTypeString(attributeInfo.getTypeInfo()))
                ).append("\n");
            }
        }

        if (sb.length() > 0) {
            sb.substring(0, sb.length() - 1);
        }
        return sb.toString();
    }

    public String params(ApiMethodInfo method) {
        return params(method, false);
    }

    public String params(ApiMethodInfo method, boolean isType) {
        StringBuilder sb = new StringBuilder();
        ArrayList<ApiMethodParamInfo> params = method.getParams();
        for (int i = 0; i < params.size(); i++) {
            ApiMethodParamInfo attributeInfo = params.get(i);
            if (attributeInfo.isFormParam() || attributeInfo.isPathVariable()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(attributeInfo.getName());
                if (isType) {
                    sb.append(":");
                    sb.append(toTypeString(attributeInfo.getTypeInfo()));
                }
            }
        }
        return sb.toString();
    }

    public String getJsPackageName() {
        return jsPackageName;
    }

    public void setJsPackageName(String jsPackageName) {
        this.jsPackageName = jsPackageName;
    }

    public String resultTypeString(ApiMethodInfo method) {
        String returnType = toTypeString(method.getResultType());
        return StringEscapeUtils.escapeHtml4(returnType);
    }
//
//    private void resultTypeString(StringBuilder sb, TypeInfo resultType) {
//        if (resultType.getTypeArguments().isEmpty()) {
//            sb.append(" type(").append(resultType.getNewJavaTypeString(true, false, false)).append(".class)");
//        } else {
//            sb.append(" type(").append(resultType.getNewJavaTypeString(true, false, false)).append(".class");//
//            for (TypeInfo typeArgument : resultType.getTypeArguments()) {
//                sb.append(",");
//                resultTypeString(sb, typeArgument);
//            }
//            sb.append(")");
//        }
//    }
}
