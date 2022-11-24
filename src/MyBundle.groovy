import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import org.osgi.framework.BundleException
import org.osgi.framework.BundleListener
import org.osgi.framework.Filter
import org.osgi.framework.FrameworkListener
import org.osgi.framework.InvalidSyntaxException
import org.osgi.framework.ServiceListener
import org.osgi.framework.ServiceReference
import org.osgi.framework.ServiceRegistration
import org.osgi.framework.Version



public class  MyBundle implements  Bundle {

    static BundleContext bundleContext = new MyBundleContext();

    @Override
    BundleContext getBundleContext() {
        if (bundleContext == null)   bundleContext = new MyBundleContext();
        return bundleContext;
    }

    @Override
    URL getEntry(String s) {
        //URL url = new URL("file://./ValueMappingFile/downloadedvalmap.xml");
        URL url = new URL("file:///C:/temp/vmap/dummy.valuemap");
        return url;
    }

    @Override
    int getState() {
        return 0
    }

    @Override
    void start(int i) throws BundleException {

    }

    @Override
    void start() throws BundleException {

    }

    @Override
    void stop(int i) throws BundleException {

    }

    @Override
    void stop() throws BundleException {

    }

    @Override
    void update(InputStream inputStream) throws BundleException {

    }

    @Override
    void update() throws BundleException {

    }

    @Override
    void uninstall() throws BundleException {

    }

    @Override
    Dictionary getHeaders() {
        return null
    }

    @Override
    long getBundleId() {
        return 1;
    }

    @Override
    String getLocation() {
        return null
    }

    @Override
    ServiceReference[] getRegisteredServices() {
        return new ServiceReference[0]
    }

    @Override
    ServiceReference[] getServicesInUse() {
        return new ServiceReference[0]
    }

    @Override
    boolean hasPermission(Object o) {
        return false
    }

    @Override
    URL getResource(String s) {
        return null
    }

    @Override
    Dictionary getHeaders(String s) {
        return null
    }

    @Override
    String getSymbolicName() {
        return 'myBundle';
    }

    @Override
    Class loadClass(String s) throws ClassNotFoundException {
        return null
    }

    @Override
    Enumeration getResources(String s) throws IOException {
        return null
    }

    @Override
    Enumeration getEntryPaths(String s) {
        return null
    }



    @Override
    long getLastModified() {
        return 0
    }

    @Override
    Enumeration findEntries(String s, String s1, boolean b) {
        return null
    }


    @Override
    Map getSignerCertificates(int i) {
        return null
    }

    @Override
    Version getVersion() {
        return null
    }
}