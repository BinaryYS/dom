
import com.alibaba.fastjson.JSON;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song.yang on 2016/12/19 11:32.
 */
public class TestDom {
    public String read(Loan loan, String path) {
        List<HesitationDay> hesitationDayList = new ArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            //InputStream in = TestDom.class.getClassLoader().getResourceAsStream("test.xml");
            Document doc = builder.parse(path);
            // root <university>
            Element root = doc.getDocumentElement();
            if (root == null) return null;
            System.err.println(root.getTagName());
            // all college node
            NodeList collegeNodes = root.getChildNodes();
            if (collegeNodes == null) return null;
            for(int i = 0; i < collegeNodes.getLength(); i++) {
                Node node = collegeNodes.item(i);
                if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                    if (node.getNodeName().equals("loanCode")){
                        System.err.println("\t" + node.getNodeName()+":"+node.getTextContent());
                        loan.setLoanCode(node.getTextContent());
                    }
                    // all class node
                    NodeList classNodes = node.getChildNodes();
                    if (classNodes == null) continue;
                    for (int j = 0; j < classNodes.getLength(); j++) {
                        Node clazz = classNodes.item(j);
                        if (clazz != null && clazz.getNodeType() == Node.ELEMENT_NODE) {
                            System.err.println("\t\t" + clazz.getNodeName());
                            // all student node
                            NodeList studentNodes = clazz.getChildNodes();
                            if (studentNodes == null) continue;
                            for (int k = 0; k < studentNodes.getLength(); k++) {
                                Node student = studentNodes.item(k);
                                if (student.getNodeName().equals("int"))
                                 System.err.println("\t\t" + student.getNodeName()+":"+student.getTextContent());
                                NodeList lastNode = student.getChildNodes();
                                if (lastNode == null) continue;
                                HesitationDay hesitationDay = new HesitationDay();
                                for (int l = 0;l <lastNode.getLength(); l++){
                                    Node last = lastNode.item(l);
                                    if (last.getNodeName().equals("loanFeeDefId")){
                                        hesitationDay.setLoanfeeDerId(last.getTextContent());
                                        System.err.println("\t\t" + last.getNodeName()+":"+last.getTextContent());
                                    }
                                    if (last.getNodeName().equals("hesitationDays")){
                                        hesitationDay.setParamVal(last.getTextContent());
                                        System.err.println("\t\t" + last.getNodeName()+":"+last.getTextContent());
                                    }
                                }
                                if (hesitationDay.getLoanfeeDerId()!=null)
                                hesitationDayList.add(hesitationDay);
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String val = JSON.toJSONString(hesitationDayList);
        return val;
    }

    public  void write() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            InputStream in = TestDom.class.getClassLoader().getResourceAsStream("test.xml");
            Document doc = builder.parse(in);
            // root <university>
            Element root = doc.getDocumentElement();
            if (root == null) return;
            // 修改属性
            root.setAttribute("name", "tsu");
            NodeList collegeNodes = root.getChildNodes();
            if (collegeNodes != null) {
                for (int i = 0; i <collegeNodes.getLength() - 1; i++) {
                    // 删除节点
                    Node college = collegeNodes.item(i);
                    if (college.getNodeType() == Node.ELEMENT_NODE) {
                        String collegeName = college.getAttributes().getNamedItem("name").getNodeValue();
                        if ("c1".equals(collegeName) || "c2".equals(collegeName)) {
                            root.removeChild(college);
                        } else if ("c3".equals(collegeName)) {
                            Element newChild = doc.createElement("class");
                            newChild.setAttribute("name", "c4");
                            college.appendChild(newChild);
                        }
                    }
                }
            }
            // 新增节点
            Element addCollege = doc.createElement("college");
            addCollege.setAttribute("name", "c5");
            root.appendChild(addCollege);
            Text text = doc.createTextNode("text");
            addCollege.appendChild(text);

            // 将修改后的文档保存到文件
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transFormer = transFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            File file = new File("src/dom-modify.xml");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            StreamResult xmlResult = new StreamResult(out);
            transFormer.transform(domSource, xmlResult);
            System.out.println(file.getAbsolutePath());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public  void write2txt(String loanCode, String val){
        StringBuilder insert = new StringBuilder("INSERT INTO  CCS_LOAN_FEE_DEF_FIXED (" + "LOAN_CODE" + "," + "FIXED_PARAM_TYPE" + ","+
                "FIXED_PARAM_TYPE_DESC" + "," + "FIXED_PARAM_VALUE" + "," + "FIXED_DATE)"+" VALUES (");
        insert.append("'"+loanCode+"','HesitationDays','犹豫期','"+val+"',"+"TO_DATE('2016-12-18 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'));\r\n\r\n");
        System.out.println(insert);
        try {
            FileWriter out = new FileWriter(new File("E:\\insert.txt"), true);
            out.write(insert.toString());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
