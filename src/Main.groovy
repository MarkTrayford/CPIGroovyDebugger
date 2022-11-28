import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.gateway.ip.core.customdev.processor.MessageImpl

//import com.sap.it.api.ITApi
//import com.sap.it.api.exception.InvalidContextException
//import com.sap.it.api.impl.ITApiFactoryRegistry
//import com.sap.it.spi.ITApiHandler
//import org.osgi.framework.BundleContext
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory

//import org.apache.commons.io.FileUtils
//import java.nio.file.Files
//import java.nio.file.Paths
//import java.util.zip.ZipFile

import java.util.zip.*

//import com.sap.it.api.ITApiFactory
import com.sap.it.api.mapping.ValueMappingApi

import com.sap.xi.mapping.camel.valmap.*;

//import com.sap.it.api.impl.ITApiFactoryRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.*;

import groovy.io.FileType


static void main(String[] args) {


    def Message message = new MessageImpl()

// Build the Value Mapping Objects from the stored files
    Bundle bundle = new MyBundle();
    BundleContext bundleContext = bundle.getBundleContext();
    def ValueMappingActivator VMActivator = new ValueMappingActivator();
    VMActivator.start(bundleContext);

    // ????Maybe I could have just done this in the first place and just used VMStore directly?????
    VMStore vmStore = VMStore.getInstance();  // maybe we can load straight into it once it's initiated.
    println("Getting Files")

    // Get each file that matches a pattern from an agreed directory
    // Each file contains one value map
    // Load the data from each into the VMStore
    // ITApiFactory will then automatically use the value
    def folder = "C:/temp/vmap/";
    def dir = new File(folder)
    Random random = new Random()
    // Random number needed for each uploaded value map file, could just be seq also if neeeded
    dir.eachFileMatch(FileType.FILES, ~/.*\.valuemap?/) {
        fname = folder + it.name
        println fname
        File initialFile = new File(fname);
        InputStream targetStream = new FileInputStream(initialFile);
        vmStore.loadValueMapping(random.nextInt(999999), targetStream);
    }
    // This is a dummy version of ITApiFactory, that is defined in this project
    // This means we can spoof the API handler otherwise I couldn't see how to use the Protected methods to add/bind a handler
    //     without recreating more of the OSGi framework
    // This way we don't need to adapt any of the users script where it uses value mapping

    ITApiFactory factory = new ITApiFactory();
    println("=====================================")

    // ##Maybe allow for passing arguments..
    //println args
    // ## Get input from console if needed
    //println "Input something"
    //println "Your input was ${System.in.newReader().readLine()}"

    // The Zip file contains the properties/header/body from a trace of a particular step
    unzipFile("zipfile.zip", message)

/* Class Structure
message
  -- properties
  -- headerproperties
  -- body

  ITApiFactory
    - APIHandler??
            -- VMStore
              - table of valuemaps
                                                                        */


    processData(message);

// Now output the props from the script
    outputMessageLogProps(message);

// Chaining multiple scripts together is possible
// Can Make subsequent calls , it will use the output from the first
//  processData2(message);
    //  processData3(message);


}

def void outputMessageLogProps(Message message) {

    println("Body:")
    println(message.body)
    println("=========================")
    println("Header:")
    def headers = message.getHeaders()
    headers.each { it -> println(it.key + "=" + it.value) }

    println("=========================")
    println("Propertys:")
    def props = message.getProperties()
    props.each { it -> println(it.key + "=" + it.value) }
    println("=========================")

    String zipFileName = "../outzipfile.zip"
    String inputDir = "logs"
    def outputDir = "zip"
    byte[] buffer = new byte[1024]
    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName))
    try {
        zos.putNextEntry(new ZipEntry("hello-world.txt"));
        zos.write("Hello World!".getBytes());
        zos.putNextEntry(new ZipEntry("hello-world2.txt"));
        zos.write("Hello World2!".getBytes());
        // not available on BufferedOutputStream
        zos.closeEntry();
    }
    finally {
        zos.close();
    }

//  ZipEntry zipEntry = zis.getNextEntry()
//  StringBuilder s = new StringBuilder();
//
//// Read each entry in the ZIP file
//  while (zipEntry != null) {
//
//    s.setLength(0);
//    println(zipEntry.name)
//    println("-----------------------------------------------")
//
//    def fileindex = zipEntry.name.lastIndexOf(".")
//    def filetype = zipEntry.name.substring(fileindex+1)
//    println filetype;
//
//    // Read all of the file into a string
//    int read = 0
//    while ((read = zis.read(buffer, 0, 1024)) >= 0) {
//      s.append(new String(buffer, 0, read));
//    }


}


def Message processData(Message message) {
    //Body

    def valueMapApi = ITApiFactory.getApi(ValueMappingApi.class, null)
    def value = valueMapApi.getMappedValue('ag1', 'id1', 'fromone', 'ag2', 'id2')
    println "returned value " + value.toString();

    println('Im in the script')
    def body = message.getBody();
    println(body)
    message.setBody("Body is modified");
    //Headers
//  def headers = message.getHeaders();
//  def value = headers.get("test");
//  println(value)
//  //  message.setHeader("oldHeader", value + " modified");
    message.setHeader("newHeader", "newHeader");
////  //Properties
//  def properties = message.getProperties();
//  value = properties.get("oldProperty");
    message.setProperty("oldProperty", "modified");
//  message.setProperty("newProperty", "newProperty");
    return message;
}

def Message processData2(Message message) {
    //Body
    println('Im in the script')
//  def body = message.getBody();
//  println(body)
//  message.setBody(body + " Body is modified");
//  //Headers
//  def headers = message.getHeaders();
//  def value = headers.get("test");
//  println(value)
//  //  message.setHeader("oldHeader", value + " modified");
//  //message.setHeader("newHeader", "newHeader");
////  //Properties
//  def properties = message.getProperties();
//  value = properties.get("oldProperty");
//  message.setProperty("oldProperty", value + " modified");
//  message.setProperty("newProperty", "newProperty");
    return message;
}


def void unzipFile(String FileName, Message message) {

//  println(System.getProperty("user.dir"))
    String zipFileName = "../zipfile.zip"
    String inputDir = "logs"
    def outputDir = "zip"

    byte[] buffer = new byte[1024]
    ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFileName))
    ZipEntry zipEntry = zis.getNextEntry()
    StringBuilder s = new StringBuilder();

// Read each entry in the ZIP file
    while (zipEntry != null) {

        s.setLength(0);
        println(zipEntry.name)
        println("-----------------------------------------------")

        def fileindex = zipEntry.name.lastIndexOf(".")
        def filetype = zipEntry.name.substring(fileindex + 1)
        println filetype;

        // Read all of the file into a string
        int read = 0
        while ((read = zis.read(buffer, 0, 1024)) >= 0) {
            s.append(new String(buffer, 0, read));
        }
        //   println(s.toString())
        //   println("\n")

        // For properties or header properties split each line and add

        switch (filetype) {
            case 'BODY':
                message.setBody(s)
            case ['PROPERTY', 'HEADER']:
                def lines = s.toString().split("\n")
                lines.each { line ->

                    if (!line.startsWith("#") && line.contains("=")) {
                        // Try and allow for = delimiter in the value too
                        int index = line.contains("=") ? line.indexOf("=") : 0;
                        def key = line.substring(0, index);
                        def value = line.substring(index + 1)
                        //     println("Key: ${key}   \t\t Value: ${value}")
                        if (filetype = 'PROPERTY') message.setProperty(key, value)
                        else message.setHeader(key, value)
                    }

                }

        }

        println('====')
        zipEntry = zis.getNextEntry()
    }
    zis.closeEntry()
    zis.close()
}

// Old code, in case I need it again, delete when finished

//def unzipFile(File file) {
//  cleanupFolder()
//  def zipFile = new ZipFile(file)
//  zipFile.entries().each { it ->
//    def path = Paths.get('c:\\folder\\' + it.name)
//    if(it.directory){
//      Files.createDirectories(path)
//    }
//    else {
//      def parentDir = path.getParent()
//      if (!Files.exists(parentDir)) {
//        Files.createDirectories(parentDir)
//      }
//      Files.copy(zipFile.getInputStream(it), path)
//    }
//  }
//}


//println(zipEntry.toString())
//    File newFile = new File(outputDir + File.separator, zipEntry.name)
//    if (zipEntry.isDirectory()) {
//      if (!newFile.isDirectory() && !newFile.mkdirs()) {
//        throw new IOException("Failed to create directory " + newFile)
//      }
//    } else {
//      // fix for Windows-created archives
//      File parent = newFile.parentFile
//      if (!parent.isDirectory() && !parent.mkdirs()) {
//        throw new IOException("Failed to create directory " + parent)
//      }
//      // write file content
//      FileOutputStream fos = new FileOutputStream(newFile)
//      text = zis.text;
//      println(text)
//      int len = 0

//Zip files
//
//ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(zipFileName))
//new File(inputDir).eachFile() { file ->
//  //check if file
//  if (file.isFile()){
//    zipFile.putNextEntry(new ZipEntry(file.name))
//    def buffer = new byte[file.size()]
//    file.withInputStream {
//      zipFile.write(buffer, 0, it.read(buffer))
//    }
//    zipFile.closeEntry()
//  }
//}
//zipFile.close()


//UnZip archive