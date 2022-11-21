
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.gateway.ip.core.customdev.processor.MessageImpl
//import org.apache.commons.io.FileUtils
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipFile

import java.util.zip.*


static void main(String[] args) {


  def Message message = new MessageImpl()

  println "Hello world2!"
  message.setBody("this is the body of the message")
  message.setHeader("test", "value")
  message.setProperty("test", "value")

  unzipFile( "zipfile.zip", message)
  processData(message);
}

def void unzipFile(String FileName, Message message) {

  println(System.getProperty("user.dir"))
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
    println(s.toString())
    println("\n")

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
                         println("Key: ${key}   \t\t Value: ${value}")
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

def Message processData(Message message) {
  //Body
  println('Im in the script')
  def body = message.getBody();
  println(body)
  message.setBody(body + " Body is modified");
  //Headers
  def headers = message.getHeaders();
  def value = headers.get("test");
  println(value)
  //  message.setHeader("oldHeader", value + " modified");
  //message.setHeader("newHeader", "newHeader");
//  //Properties
  def properties = message.getProperties();
  value = properties.get("oldProperty");
  message.setProperty("oldProperty", value + " modified");
  message.setProperty("newProperty", "newProperty");
  return message;
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



//class Message {
//  public void setBody(String body) {
//  }
//  public String getBody() {
//  }
//  public String getHeaders() {
//  }
//  public String setHeaders(String header) {
//  }

//}


import java.util.HashMap;
