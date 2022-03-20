package top.greatbiscuit.hyxk.utils;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.util.Arrays;

/**
 * 处理Markdown的工具类
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/20 17:09
 */
public class MarkdownUtil {

    /**
     * 将Markdown转为Html
     *
     * @param markdown
     * @return
     */
    public static String markdown2Html(String markdown) {
        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.MARKDOWN);

        // 开启表格转换
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));


        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

}
