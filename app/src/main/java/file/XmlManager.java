package file;

import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import drag.Shape;

/**
 * Created by taozhiheng on 15-3-26.
 */
public class XmlManager {

    /**
     * 初始化xml文件，没有则创建文件，并写入根几点数据
     * @param
     * @return
     */
    private String filePath;

    public XmlManager(String filePath)
    {
        this.filePath = filePath;
    }

    public void init(){
        File file = new File(filePath);
        if(!file.exists())
        {
            try {
                file.createNewFile();
                initXmlFile(file);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * 初始化xml文件
     * @para
     * @return
     */
    public void initXmlFile(File file){
        XmlSerializer serialize= Xml.newSerializer();
        StringWriter writer=new StringWriter();
        try {
            serialize.setOutput(writer);
            serialize.startDocument("UTF-8", true);
            serialize.startTag("", "info");
            serialize.attribute(null, "finish", "false");
            serialize.endTag("", "info");
            serialize.endDocument();
            OutputStream os = new FileOutputStream(file);
            OutputStreamWriter ow = new OutputStreamWriter(os);
            ow.write(writer.toString());
            ow.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * 读取传入的路径，返回一个document对象
     * @return document
     */
    public Document loadInit(){
        Document document;
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new File(filePath));
            document.normalize();
            return document;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 增加一个circle节点
     * @param shape
     * @return 是否增加成功，true为增加成功，false为增加失败
     */
    public boolean addCircle(Shape shape)
    {
        try{
            //读取传入的路径，返回一个document对象
            Document document = loadInit();
            //创建叶节点
            Element eltSession = document.createElement("circle");
            Element id = document.createElement("id");//创建叶节点的第一个元素
            Element centerX = document.createElement("centerX");//创建叶节点的第一个元素
            Element centerY = document.createElement("centerY");//创建叶节点的第二个元素
            Element title = document.createElement("title");//创建叶节点的第三个元素
            Text idValue = document.createTextNode(""+ shape.getId());//创建叶节点的第一个元素下的文本节点
            id.appendChild(idValue);//把该文本节点加入到叶节点的第一个元素里面
            Text xValue = document.createTextNode(""+ shape.getCenterX());//创建叶节点的第一个元素下的文本节点
            centerX.appendChild(xValue);//把该文本节点加入到叶节点的第一个元素里面
            Text yValue = document.createTextNode(""+ shape.getCenterY());//创建叶节点的第二个元素下的文本节点
            centerY.appendChild(yValue);//把该文本节点加入到叶节点的第二个元素里面
            Text titleValue = document.createTextNode(shape.getTitle());//创建叶节点的第三个元素下的文本节点
            title.appendChild(titleValue);//把该文本节点加入到叶节点的第三个元素里面
            //把叶节点下的元素加入到叶节点下
            eltSession.appendChild(centerX);
            eltSession.appendChild(centerY);
            eltSession.appendChild(title);

            //获取根节点
            Element eltRoot = document.getDocumentElement();
            //把叶节点加入到根节点下
            eltRoot.appendChild(eltSession);
            Log.d("drag", "add circle");
            //更新修改后的源文件
            saveXML(document, filePath);
            return true;
        }catch(Exception e){
            e.printStackTrace();
//            System.out.println(e.getMessage());
            return false;
        }


    }

    /**
     * 删除最后一个circle节点
     * @return 是否删除成功，true为删除成功，false为删除失败
     */
    public boolean delCircle(){

        Document document = loadInit();
        try{
            NodeList sessionList = document.getElementsByTagName("circle");
            Node node = sessionList.item(sessionList.getLength()-1);
            node.getParentNode().removeChild(node);
            saveXML(document, filePath);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除所有Session节点     *
     * @return 是否删除成功，true为删除成功，false为删除失败
     */
    public boolean delAllCircleList(){
        try {
            File file = new File(filePath);
            initXmlFile(file);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;

    }

    /**
     *

    /**
     * 更新一个Session节点
     *  @param shape
     * @return 是否更新成功，true为更新成功，false为更新失败
     */
    public boolean updateCircle(Shape shape){
        //读取传入的路径，返回一个document对象
        Document document = loadInit();
        try{
            //获取叶节点
//            NodeList sessionList = document.getElementsByTagName("circle");
//            //遍历叶节点
//            Node node = sessionList.item(circle.getId());
//            node.getChildNodes().item(1).setNodeValue(String.valueOf(circle.getCenterX()));
//            node.getChildNodes().item(2).setNodeValue(String.valueOf(circle.getCenterY()));
//            node.getChildNodes().item(3).setNodeValue(circle.getTitle());
//            saveXML(document, filePath);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }


    }

    /**
     * 把修改后的document写进源文件（更新源文件）
     * @param document
     * @param filePath
     * @return
     */
    public boolean saveXML(Document document, String filePath){
        try{
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取xml文件的所有记录
     * @return
     */
    public List<Shape> loadCircleList(){
        List<Shape> sessionList = new ArrayList<>();
        try{
            //读取传入的路径，返回一个document对象
            Document document = loadInit();
            //获取叶节点
            NodeList nodeList = document.getElementsByTagName("circle");
            //遍历叶节点
            for(int i=0; i<nodeList.getLength(); i++){
                int id = Integer.parseInt(document.getElementsByTagName("id").item(i).getFirstChild().getNodeValue());
                float centerX = Float.parseFloat(document.getElementsByTagName("centerX").item(i).getFirstChild().getNodeValue());
                float centerY = Float.parseFloat(document.getElementsByTagName("centerY").item(i).getFirstChild().getNodeValue());
                String title =  document.getElementsByTagName("title").item(i).getFirstChild().getNodeValue();
                //sessionList.add(new Circle(null, id, centerX, centerY, title, null));
            }
            return sessionList;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
