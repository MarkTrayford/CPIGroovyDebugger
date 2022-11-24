
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

//  def ITApiFactoryRegistry factoryReg = new ITApiFactoryRegistry();
      // need to bind somehow the factory into this register

  //def VMStore

  // Create a bundle context to pass to the activator

//  Bundle bundle = FrameworkUtil.getBundle();
//  BundleContext context = bundle.getBundleContext();


  Bundle bundle = new MyBundle();
  BundleContext bundleContext = bundle.getBundleContext();
  def ValueMappingActivator VMActivator = new ValueMappingActivator();
  VMActivator.start(bundleContext);

  // ????Maybe I could have just done this in the first place and just used VMStore directly?????
  VMStore vmStore = VMStore.getInstance();  // maybe we can load straight into it once it's initiated.
  //InputStream inputStream = new InputStream(new FileInputStream("file:///C:/temp/text.xml"))
//   inputStream = new File("file:///C:/temp/text.xml")

    println("Getting Files")
  def folder = "C:/temp/vmap/";
  def dir = new File(folder)
  dir.eachFileMatch(FileType.FILES, ~/.*\.valuemap/ ) {
    println it.name
  }
//  dir.eachFileMatch(FileType.ANY) {
//  dir.each {
//    println it.name
//  }
  println("=====================================")


  File initialFile = new File("C:/temp/vmap/text2.valuemap");
  InputStream targetStream = new FileInputStream(initialFile);
  vmStore.loadValueMapping(2342434, targetStream);

  initialFile = new File("C:/temp/vmap/testvaluemap.valuemap");
  targetStream = new FileInputStream(initialFile);
  vmStore.loadValueMapping(345345345, targetStream);

  ITApiFactory  factory = new ITApiFactory();
 // def ValueMappingApiService service = new ValueMappingApiService();

  //println(valueMapApi.GetClass())

  // ##Maybe allow for passing arguments..
  //println args
 // ## Get input from console if needed
  //println "Input something"
  //println "Your input was ${System.in.newReader().readLine()}"

  unzipFile( "zipfile.zip", message)
  processData(message);
  processData2(message);
}



def Message processData(Message message) {
  //Body

  def valueMapApi = ITApiFactory.getApi(ValueMappingApi.class, null)
  def value = valueMapApi.getMappedValue('ag1', 'id1', 'fromone', 'ag2', 'id2')
  println "returned value " + value.toString() ;

  println('Im in the script')
  def body = message.getBody();
  println(body)
  message.setBody(body + " Body is modified");
  //Headers
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
  while (zipEntry != null) {
    s.setLength(0);
    println(zipEntry.name)
    println("-----------------------------------------------")

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
    int read = 0
    while ((read = zis.read(buffer, 0, 1024)) >= 0) {
      s.append(new String(buffer, 0, read));
    }
 //   println(s.toString())
 //   println("\n")

    def lines = s.toString().split("\n")

    lines.each { line ->

      if ( !line.startsWith("#") && line.contains("="))
      {
        //println(line);
        //   println("contains = ")
        //def (key, value) = line.tokenize('=')

        // Try and allow for = delimiter in the value too
        int index = line.contains("=") ? line.indexOf("=") : 0;
        def key = line.substring(0, index);
        def value = line.substring(index+1)
   //     println("Key: ${key}   \t\t Value: ${value}")
        message.setProperty(key, value)
      }
   }

    println('====')
//      while ((len = zis.read(buffer)) > 0) {
//        s.append(new String(buffer, 0, read));
////          println(buffer)
//      }
//      fos.close()
//    }
    zipEntry = zis.getNextEntry()
  }
  zis.closeEntry()
  zis.close()
}


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


