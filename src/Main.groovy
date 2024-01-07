
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.gateway.ip.core.customdev.processor.MessageImpl
import com.sap.it.op.agent.api.Exchange
import com.sap.it.op.agent.collector.camel.impl.MplAttachmentWriterImpl
import com.sap.it.op.agent.mpl.factory.impl.MessageLogFactoryImpl
import org.apache.camel.Attachment
import org.apache.camel.CamelContext
import org.apache.camel.InvalidPayloadException
import org.apache.camel.TypeConverter
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.spi.TypeConverterRegistry
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

        // look into using args and different Run configurations to control options
        println(args)
        // Define the message object
        def CamelContext camelContext = new DefaultCamelContext()
        // The exchange is the level above a message, and contains the message
        def Exchange exchange = new DefaultExchange(camelContext)

        // Need to work out the difference between the camelMessage and SAP version
        org.apache.camel.Message camelMessage = new org.apache.camel.impl.DefaultMessage(camelContext)

        def Message message = new MessageImpl(exchange)  // #TODO do I still need this?
        exchange.setIn(camelMessage)

//      convertors are needed to manage the different classes for Body, eg String , Stream and the conversions between them
//       #TODO Can't remember where I got to with this, do I still need this
//       #TODO Need to test getting the bodyin as a string, stream etc. so we don't have to adapt any scripts
//        println("start convertor")
        TypeConverterRegistry tcr = camelContext.getTypeConverterRegistry();
//        TypeConverter tc = tcr.lookup(Document.class, InputStream.class);
//        tcr.listAllTypeConvertersFromTo();
        //tcr.addTypeConverter(java.lang.String, java.lang.String, )
//        println("end convertor")
// Set up the content of the message properties/mapping/logs
        createvmapdummy(); // Need a dummy value map to initiate the standard classes
        CreateValueMappings(); // Read the value mapping directory and build value map in the Context
        CreateMessageLogFactory(); // Create a MessageLog
        ReadProperties(message) // Read file and setup properties, headers and body for the message
        camelMessage.setBody(message.getBody()) // Need to do this so GetBody work, if it needs a convertor then it looks to the camelMessage
        outputMessageLogProps(message); // Show
        //println("body class")
        //println(message.body.class)
        processData(message);
        outputMessageLogProps(message);
        return;


    }

// Create a dummy value map file with a random guid
// This is needed as the value map api will not work without a value map file
    def void createvmapdummy() {

  //      Files.createDirectories(Paths.get("C:/temp/vmap/"));
        def dummyfilecontent = "<vm version=\"2.0\"><group id=\"117ab65bb4e8deb73e71ef2d21686bbb\"></group></vm>"
        def dummyfile = new File("C:/CPIViewer/DataDump/VMAP/dummy.valuemap")
   //     dummyfile.mkdirs()
        dummyfile.write(dummyfilecontent)
//

    }

    def Message processData(Message message) {

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

        message.setHeader("newHeader", "newHeader");
        message.setProperty("oldProperty", "modified");
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
        def folder = "C:/CPIViewer/DataDump/VMAP/";
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

    }


    def void ReadProperties(Message message) {

//  println(System.getProperty("user.dir"))
        // need to set some dummy to stop them being null, #HACK need better way to do this
        message.setHeader( "dummy", "")
        message.setBody( "" )
        message.setProperty("dummy", "dummy")

        String zipFileName = "C:/CPIViewer/DataDump/Debug/Properties/DebugProperties.zip"
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
        def folder = "C:/CPIViewer/DataDump/Debug/Properties/";
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

