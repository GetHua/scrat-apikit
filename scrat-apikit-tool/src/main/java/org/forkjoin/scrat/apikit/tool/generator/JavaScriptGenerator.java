package org.forkjoin.scrat.apikit.tool.generator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FileUtils;
import org.forkjoin.scrat.apikit.tool.Utils;
import org.forkjoin.scrat.apikit.tool.info.ApiInfo;
import org.forkjoin.scrat.apikit.tool.info.MessageInfo;
import org.forkjoin.scrat.apikit.tool.utils.JsonUtils;
import org.forkjoin.scrat.apikit.tool.wrapper.BuilderWrapper;
import org.forkjoin.scrat.apikit.tool.wrapper.JSApiWrapper;
import org.forkjoin.scrat.apikit.tool.wrapper.JSMessageWrapper;
import org.forkjoin.scrat.apikit.tool.wrapper.JSWrapper;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 这个版本还不能 ,解开泛型或者支持泛型
 */
public class JavaScriptGenerator extends HttlGenerator {
    private JSWrapper.Type type = JSWrapper.Type.CommonJS;
    private String jsPackageName;

    public JavaScriptGenerator(String jsPackageName) {
        this.jsPackageName = jsPackageName;
    }

    protected File getFileName(BuilderWrapper utils) {
        return Utils.packToPath(outPath, utils.getPack(), utils.getName(), ".js");
    }

    protected File getTsDFileName(BuilderWrapper utils) {
        return Utils.packToPath(outPath, utils.getPack(), utils.getName(), ".d.ts");
    }


    protected File getMessageFileName(BuilderWrapper utils) {
        return Utils.packToPath(outPath, utils.getPack(utils.getDistPackage()), utils.getName(), ".js");
    }

    protected File getTsDMessageFileName(BuilderWrapper utils) {
        return Utils.packToPath(outPath, utils.getPack(utils.getDistPackage()), utils.getName(), ".d.ts");
    }

    protected String getTempl(String name) {
        return "/org/forkjoin/scrat/apikit/tool/generator/js/" + type.name() + "/" + name;
    }

    @Override
    public void generateApi(ApiInfo apiInfo) throws Exception {
        JSApiWrapper utils = new JSApiWrapper(context, apiInfo, rootPackage, jsPackageName);
        File dFile = getTsDFileName(utils);
        File file = getFileName(utils);
        utils.setType(type);
        utils.init();

        executeModule(
                utils,
                getTempl("ApiItem.httl"),
                file
        );

        executeModule(
                utils,
                getTempl("ApiItem.d.httl"),
                dFile
        );
    }

    @Override
    protected BuilderWrapper<MessageInfo> createMessageWarpper(MessageInfo messageInfo, String distPackage, String distName) {
        JSMessageWrapper jsMessageWrapper = new JSMessageWrapper(context, messageInfo, rootPackage);
        jsMessageWrapper.setDistName(distName);
        jsMessageWrapper.setDistPackage(distPackage);
        jsMessageWrapper.setType(type);
        return jsMessageWrapper;
    }


    @Override
    public void generateMessage(BuilderWrapper<MessageInfo> utils) throws Exception {
        File dFile = getTsDMessageFileName(utils);
        File file = getMessageFileName(utils);

        utils.init();
        executeModule(
                utils,
                getTempl("Message.d.httl"),
                dFile
        );

        FileUtils.write(file, "", "utf8");
    }

    public void copyTool(String name) throws Exception {
        File file = new File(outPath, name);
        FileUtils.copyInputStreamToFile(
                JavaScriptGenerator.class.getResourceAsStream(
                        "/org/forkjoin/scrat/apikit/tool/generator/js/" + type.name() + "/tool/" + name
                ),
                file
        );
    }

    @Override
    public void generateTool() throws Exception {
        //copy 工具文件
//        copyTool("AbstractApi.d.ts");
//        copyTool("AbstractApi.js");
//
//        copyTool("RequestGroup.d.ts");
//        copyTool("RequestGroup.js");
//
//        copyTool("RequestGroupImpi.d.ts");
//        copyTool("RequestGroupImpi.js");
//
//        copyTool("Request.d.ts");
//        copyTool("Request.js");
//
//        copyTool("HttpUtils.d.ts");
//        copyTool("HttpUtils.js");

        {
            Map<String, Object> parameters = new HashMap<>();

//            Set<Map.Entry<String, Collection<MessageInfo>>> all = context.getMessages().getAll().entrySet();
//            parameters.put("all", all);
            parameters.put("values", context.getMessages());
            parameters.put("apis", context.getApis().getValues());
            File dFile = Utils.packToPath(outPath, "", "index", ".d.ts");
            File file = Utils.packToPath(outPath, "", "index", ".js");

            execute(
                    parameters,
                    getTempl("index.d.httl"),
                    dFile
            );

            execute(
                    parameters,
                    getTempl("index.httl"),
                    file
            );
        }
        {
            File packageFile = Utils.packToPath(outPath, "", "package", ".json");
            ObjectNode packageJson;
            if (packageFile.exists()) {
                packageJson = (ObjectNode) JsonUtils.mapper.readTree(packageFile);
            } else {
                try (InputStream inputStream = JavaScriptGenerator.class.getResourceAsStream(getTempl("package.json"))) {
                    packageJson = (ObjectNode) JsonUtils.mapper.readTree(inputStream);
                }
            }

            packageJson.put("name", jsPackageName);
            if (this.version != null) {
                String prevVersionText = packageJson.get("version").asText();
                if (prevVersionText != null) {
                    prevVersionText = prevVersionText.replaceAll("([^.]+)\\.([^.]+)\\.([^.]+)", "$1.$2." + version);
                } else {
                    prevVersionText = "1.0." + version;
                }
                packageJson.put("version", prevVersionText);
            }
            JsonUtils.mapper.writerWithDefaultPrettyPrinter().writeValue(packageFile, packageJson);
        }
//        {
//            Map<String, Object> parameters = new HashMap<>();
//            Collection<ApiInfo> values = context.getApis().getValues();
//            parameters.put("values", values);
//            parameters.put("nameUtils", new NameUtils());
//            File dFile = Utils.packToPath(outPath, "", "Apis", ".d.ts");
//            execute(
//                    parameters,
//                    getTempl("Apis.d.httl"),
//                    dFile
//            );
//            File file = Utils.packToPath(outPath, "", "Apis", ".js");
//            execute(
//                    parameters,
//                    getTempl("Apis.httl"),
//                    file
//            );
//        }
    }

    public JSWrapper.Type getType() {
        return type;
    }

    public void setType(JSWrapper.Type type) {
        this.type = type;
    }
}
