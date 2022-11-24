import com.sap.it.api.ITApi
import com.sap.it.api.exception.InvalidContextException
import com.sap.it.api.impl.ITApiFactoryRegistry
import com.sap.it.spi.ITApiHandler
import com.sap.xi.mapping.camel.valmap.ValueMappingApiService
import org.slf4j.Logger
import org.slf4j.LoggerFactory


public final class ITApiFactory {
    private static final Logger TRACE = LoggerFactory.getLogger(com.sap.it.api.ITApiFactory.class);
    // This will start a VMStore object, the static constructor of that will load the mapping
    public static ITApi myApiService = new ValueMappingApiService();
    // public static ValueMappingApiService myapi = new ValueMappingApiService();
    private ITApiFactory() {
    }
    /** @deprecated */
    @Deprecated
    public static <T extends ITApi> T getApi(Class<? extends ITApi> apiType, Object context) throws InvalidContextException {
        return myApiService;
        //return getServiceInternal(apiType, context);
    }
    public static <T extends ITApi> T getService(Class<? extends ITApi> apiType, Object context) throws InvalidContextException {
        return myApiService;
        //return getServiceInternal(apiType, context);
    }
    private static <T extends ITApi> T getServiceInternal(Class<? extends ITApi> apiType, Object context) throws InvalidContextException {
        ITApiHandler<? extends ITApi> apiFactory = ITApiFactoryRegistry.getApiHandler(apiType);
        if (apiFactory != null) {
            TRACE.debug("Successfully retrieved API Factory for the apiType: {}", apiType);
            return apiFactory.getApi(context);
        } else {
            TRACE.error("Unable to retrieve API Factory for the apiType: {}", apiType);
            throw new InvalidContextException("Exception occurred while trying to retrieve API Factory for the apiType: " + apiType.toString());
        }
    }
}
