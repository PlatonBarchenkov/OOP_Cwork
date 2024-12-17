package com.bichpormak.data;

import com.bichpormak.models.Teacher;
import com.bichpormak.models.Student;
import com.bichpormak.exceptions.DataLoadException;
import com.bichpormak.exceptions.DataSaveException;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.SAXException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Класс для загрузки и сохранения данных из/в XML-файлы.
 */
public class DataManager {

    private JFrame frame;

    public DataManager(JFrame frame) {
        this.frame = frame;
    }

    /**
     * Загрузка данных из XML-файла.
     *
     * @param xmlFile Файл для загрузки данных.
     * @param teachers Список для добавления учителей.
     * @param students Список для добавления учеников.
     * @throws DataLoadException Если возникает ошибка при загрузке данных.
     */
    public void loadDataFromFile(File xmlFile, List<Teacher> teachers, List<Student> students) throws DataLoadException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            // Нормализация документа
            doc.getDocumentElement().normalize();

            // Загрузка учителей
            NodeList teacherList = doc.getElementsByTagName("teacher");
            for (int i = 0; i < teacherList.getLength(); i++) {
                Element teacherElement = (Element) teacherList.item(i);
                String name = teacherElement.getAttribute("name");
                String subject = teacherElement.getAttribute("subject");
                String classes = teacherElement.getAttribute("classes");

                Teacher teacher = new Teacher(name, subject, classes);
                teachers.add(teacher);
            }

            // Загрузка учеников
            NodeList studentList = doc.getElementsByTagName("student");
            for (int i = 0; i < studentList.getLength(); i++) {
                Element studentElement = (Element) studentList.item(i);
                String name = studentElement.getAttribute("name");
                String studentClass = studentElement.getAttribute("class");
                String performance = studentElement.getAttribute("performance");

                Student student = new Student(name, studentClass, performance);
                students.add(student);
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new DataLoadException("Ошибка при загрузке данных: " + e.getMessage());
        }
    }

    /**
     * Сохранение данных в XML-файл.
     *
     * @param xmlFile Файл для сохранения данных.
     * @param teachers Список учителей.
     * @param students Список учеников.
     * @throws DataSaveException Если возникает ошибка при сохранении данных.
     */
    public void saveDataToFile(File xmlFile, List<Teacher> teachers, List<Student> students) throws DataSaveException {
        try {
            // Создание фабрики и построителя документов
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Создание нового документа
            Document doc = docBuilder.newDocument();

            // Создание корневого элемента <school>
            Element rootElement = doc.createElement("school");
            doc.appendChild(rootElement);

            // Создание элемента <teachers>
            Element teachersElement = doc.createElement("teachers");
            rootElement.appendChild(teachersElement);

            // Добавление каждого учителя как элемента <teacher>
            for (Teacher teacher : teachers) {
                Element teacherElement = doc.createElement("teacher");
                teacherElement.setAttribute("name", teacher.getName());
                teacherElement.setAttribute("subject", teacher.getSubject());
                teacherElement.setAttribute("classes", teacher.getClasses());
                teachersElement.appendChild(teacherElement);
            }

            // Создание элемента <students>
            Element studentsElement = doc.createElement("students");
            rootElement.appendChild(studentsElement);

            // Добавление каждого ученика как элемента <student>
            for (Student student : students) {
                Element studentElement = doc.createElement("student");
                studentElement.setAttribute("name", student.getName());
                studentElement.setAttribute("class", student.getStudentClass());
                studentElement.setAttribute("performance", student.getPerformance());
                studentsElement.appendChild(studentElement);
            }

            // Создание преобразователя и запись документа в файл
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            // Для красивого форматирования XML
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException e) {
            throw new DataSaveException("Ошибка при сохранении данных: " + e.getMessage());
        }
    }
}
