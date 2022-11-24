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

public class MyBundleContext implements  BundleContext {
    MyBundle bundle = new MyBundle();

    @Override
    String getProperty(String var1) {
        return 'DummypropertyValue';
    }

    @Override
    Bundle getBundle() {
        return bundle;
    }

    @Override
    Bundle installBundle(String s, InputStream inputStream) throws BundleException {
        return bundle;
    }

    @Override
    Bundle installBundle(String s) throws BundleException {
        return bundle;
    }

    @Override
    Bundle getBundle(long l) {
        return bundle;
    }

    @Override
    Bundle[] getBundles() {
        return new MyBundle[0]
    }

    @Override
    void addServiceListener(ServiceListener serviceListener, String s) throws InvalidSyntaxException {
        def x = 1
    }

    @Override
    void addServiceListener(ServiceListener serviceListener) {
        def x = 1
    }

    @Override
    void removeServiceListener(ServiceListener serviceListener) {
        def x = 1
    }

    @Override
    void addBundleListener(BundleListener bundleListener) {
        def x = 1
    }

    @Override
    void removeBundleListener(BundleListener bundleListener) {
        def x = 1
    }

    @Override
    void addFrameworkListener(FrameworkListener frameworkListener) {
        def x = 1
    }

    @Override
    void removeFrameworkListener(FrameworkListener frameworkListener) {
        def x = 1
    }

    @Override
    ServiceRegistration registerService(String[] strings, Object o, Dictionary dictionary) {
        return null
    }

    @Override
    ServiceRegistration registerService(String s, Object o, Dictionary dictionary) {
        return null
    }

    @Override
    ServiceReference[] getServiceReferences(String s, String s1) throws InvalidSyntaxException {
        return new ServiceReference[0]
    }

    @Override
    ServiceReference[] getAllServiceReferences(String s, String s1) throws InvalidSyntaxException {
        return new ServiceReference[0]
    }

    @Override
    ServiceReference getServiceReference(String s) {
        return null
    }

    @Override
    Object getService(ServiceReference serviceReference) {
        return null
    }

    @Override
    boolean ungetService(ServiceReference serviceReference) {
        return false
    }

    @Override
    File getDataFile(String s) {
        return null
    }

    @Override
    Filter createFilter(String s) throws InvalidSyntaxException {
        return null
    }
}
