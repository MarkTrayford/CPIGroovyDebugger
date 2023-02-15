
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.gateway.ip.core.customdev.processor.MessageImpl
import com.sap.it.op.agent.api.Exchange
import com.sap.it.op.agent.collector.camel.impl.MplAttachmentWriterImpl
import com.sap.it.op.agent.mpl.factory.impl.MessageLogFactoryImpl
import org.apache.camel.Attachment
import org.apache.camel.CamelContext
import org.apache.camel.InvalidPayloadException
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.cxf.message.ExchangeImpl
import org.osgi.framework.*
import org.apache.camel.Exchange

import javax.activation.DataHandler
import java.util.function.Supplier;

//import org.apache.commons.io.FileUtils
//import java.nio.file.Files
//import java.nio.file.Paths
//import java.util.zip.ZipFile

import java.util.zip.*

//import com.sap.it.api.ITApiFactory
import com.sap.it.api.mapping.ValueMappingApi

import com.sap.xi.mapping.camel.valmap.*;

import com.sap.it.api.impl.ITApiFactoryRegistry;

import groovy.io.FileType;

//import Scripts.MyScriptCollection.GroovyFileOne.*

    static void main(String[] args) {

        // Define the message object
        def CamelContext camelContext = new DefaultCamelContext()
        def Exchange exchange = new DefaultExchange(camelContext)  // Not sure if we need the exchange for anything
        org.apache.camel.Message camelMessage = new org.apache.camel.impl.DefaultMessage(camelContext)
        def Message message = new MessageImpl(exchange)


        // Set up the content of the message properties/mapping/logs
        CreateValueMappings();
        CreateMessageLogFactory();
        ReadProperties(message)
        //outputMessageLogProps(message);

        //processData(message);

        /// **** If no class is defined in the script then the class name is the name of the file eg scriptxyz.groovy makes a scriptxyz class
        // The files are saved with the ScriptCollection or Flow as the folder name
        // MyScriptCollection
        //    -- MyScriptCollection_script1.groovy ( = script1.groovy ) they have to have a unique filename as this is the class
        //    -- MyScriptCollection_script2.groovy ( = script1.groovy ) they have to have a unique filename as this is the class

      // def instance1 = new FirstTest_script1()
       // instance1.processData(message);

        // Read the meta file to get the name of the selected script for debugging
        // Format is :    MyScriptCollection:Script1:processData ( collectionname:filename:functionname )
        String metaDataFile = 'C:/temp/CPIDataDump/Debug/processMetaData.process'
        def file = new File(metaDataFile)
        def contents = file.text
        def (dirName, scriptFile, functionName) = contents.tokenize(":")
        def ( classSection) = scriptFile.tokenize(".")
        // Create an instance of the class dynamically ( class is the same as filename = collection_script.groovy )
        def classname = dirName + "_" + classSection;
        println "classname = $classname"

        // Create an instance of the class then execute the function within
        def instance = this.class.classLoader.loadClass( classname, true, false )?.newInstance()
        instance.invokeMethod(functionName, message)

        //Binding b  = new Binding([ITApiFactory: ITApiFactory])
        //b.setVariable(ITApiFactory, ITApiFactory)

//        String fileName = "C:/temp/CPIDataDump/Scripts/${dirName}/${scriptFile}"
//        String fileName = "./Scripts/${dirName}/${scriptFile}"
//        String fileName = "./scriptx.groovy"
//        println("Getting script file from " + fileName)
//        println("Calling function" + functionName)
//        GroovyShell shell = new GroovyShell()
//        // Change the script so it doesn't import the standard sap ITApifactory, that will force it to use the fake one from this folder
//        def script1 = shell.parse(new File(fileName).text.replace("import com.sap.it.api.ITApiFactory", "//import com.sap.it.api.ITApiFactory"))
//        script1.processData(message)
        // println("===Starting Script ====")
        //  script1.invokeMethod(functionName, message)
        // println("===End of Script ====")

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Now output the props from the script
        //outputMessageLogProps(message);



    }

    def Message processData(Message message) {
        //Body

        println('Exchange ' + message.payload.getClass() )
        println('Im in the script xxx')
        def body = message.getBody(String.class);
        println(body)
        message.setBody("Body is modified");

        def valueMapApi = ITApiFactory.getApi(ValueMappingApi.class, null)
        def value = valueMapApi.getMappedValue('ag1', 'id1', 'fromone', 'ag2', 'id2')
        println "returned value " + value.toString();

        def messageLog = messageLogFactory.getMessageLog(message);
        if (messageLog != null) {
            messageLog.setStringProperty("Logging#1", "Printing Payload As Attachment")
            messageLog.addAttachmentAsString("ResponsePayload:", body, "text/plain");
        }


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
    def void CreateMessageLogFactory() {

        def mplAttachmentWriter = new MplAttachmentWriterImpl();
        messageLogFactory = new MessageLogFactoryImpl();
        messageLogFactory.bindMplAttachmentWriter(mplAttachmentWriter);

    }

    def void CreateValueMappings() {
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
        def folder = "C:/temp/CPIDataDump/ValueMaps/";
        def dir = new File(folder)
        Random random = new Random()
        // Random number needed for each uploaded value map file, could just be seq also if neeeded
        dir.eachFileMatch(FileType.FILES, ~/.*\.xml?/) {
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
    }


    def void outputMessageLogProps(Message message) {

        println("===============================")
        println("Exchange Body,Header and Property Values")
        println("BODY:")
        println(message.body)
        println("=========================")
        println("HEADER:")
        def headers = message.getHeaders()
        headers.each { it -> println(it.key + "=" + it.value) }

        println("=========================")
        println("PROPERTY:")
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


    def void ReadProperties(Message message) {

//  println(System.getProperty("user.dir"))
        String zipFileName = "C:/temp/CPIDataDump/Debug/Properties/DebugProperties.zip"
        String inputDir = "logs"
        def outputDir = "zip"

        byte[] buffer = new byte[1024]
        try {
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
                processPropString(message, filetype, s.toString())
                println('====')
                zipEntry = zis.getNextEntry()
            }
            zis.closeEntry()
            zis.close()
        }
        catch (Exception e) {
            println("ZIP file not found " + e)
        }

        // Look for individual files
        println("Reading individual property files")
        def folder = "C:/temp/CPIDataDump/Debug/Properties/";
        def dir = new File(folder)
        dir.listFiles().each { file ->
            println(file)
            fileindex = file.name.lastIndexOf(".")
            filetype = file.name.substring(fileindex + 1)
            println filetype;
            if (filetype in ["body", "header", "properties"]) {
                // Read all of the file into a string
                processPropString(message, filetype, file.text)
            }
        }

    }

    def processPropString(Message _message, String _filetype, String _Content) {
        // For properties or header properties split each line and add
        switch (_filetype) {
            case 'body':
                _message.setBody(_Content)
            case ['properties', 'header']:
                def lines = _Content.split("\n")
                lines.each { line ->

                    if (!line.startsWith("#") && line.contains("=")) {
                        // Try and allow for = delimiter in the value too
                        int index = line.contains("=") ? line.indexOf("=") : 0;
                        def key = line.substring(0, index);
                        def value = line.substring(index + 1)

                        println(_filetype + " Key: ${key}   \t\t Value: ${value}")
                        if (_filetype == 'properties') _message.setProperty(key, value)
                        else _message.setHeader(key, value)
                    }

                }
        }

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

