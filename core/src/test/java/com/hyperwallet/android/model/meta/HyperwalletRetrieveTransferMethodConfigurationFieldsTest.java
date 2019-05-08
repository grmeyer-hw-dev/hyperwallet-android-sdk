package com.hyperwallet.android.model.meta;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.emptyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static com.hyperwallet.android.util.HttpMethod.POST;

import com.hyperwallet.android.Hyperwallet;
import com.hyperwallet.android.exception.HyperwalletException;
import com.hyperwallet.android.listener.HyperwalletListener;
import com.hyperwallet.android.model.HyperwalletError;
import com.hyperwallet.android.model.HyperwalletErrors;
import com.hyperwallet.android.model.meta.field.EDataType;
import com.hyperwallet.android.model.meta.field.HyperwalletField;
import com.hyperwallet.android.model.meta.query.HyperwalletTransferMethodConfigurationFieldQuery;
import com.hyperwallet.android.rule.HyperwalletExternalResourceManager;
import com.hyperwallet.android.rule.HyperwalletMockWebServer;
import com.hyperwallet.android.rule.HyperwalletSdkMock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;

import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.RecordedRequest;

@RunWith(RobolectricTestRunner.class)
public class HyperwalletRetrieveTransferMethodConfigurationFieldsTest {

    @Rule
    public HyperwalletMockWebServer mServer = new HyperwalletMockWebServer();
    @Rule
    public HyperwalletSdkMock mSdkMock = new HyperwalletSdkMock(mServer);
    @Rule
    public final HyperwalletExternalResourceManager mHyperwalletResourceManager =
            new HyperwalletExternalResourceManager();
    @Rule
    public MockitoRule mMockito = MockitoJUnit.rule();

    @Mock
    private HyperwalletListener<HyperwalletTransferMethodConfigurationFieldResult> mListener;
    @Captor
    private ArgumentCaptor<HyperwalletException> mExceptionCaptor;
    @Captor
    private ArgumentCaptor<TransferMethodConfigurationResult> mFieldsResultCaptor;

    private CountDownLatch mAwait = new CountDownLatch(1);

    @Test
    public void testRetrieveTransferMethodConfigurationFields_returnsFields()
            throws InterruptedException {
        String responseBody = mHyperwalletResourceManager.getResourceContent(
                "tmc_configuration_connection_response.json");
        mServer.mockResponse().withHttpResponseCode(HttpURLConnection.HTTP_OK).withBody(responseBody).mock();
        Hyperwallet.getDefault().retrieveTransferMethodConfigurationFields(
                new HyperwalletTransferMethodConfigurationFieldQuery("US",
                        "USD",
                        "BANK_ACCOUNT",
                        "INDIVIDUAL"), mListener);
        mAwait.await(500, TimeUnit.MILLISECONDS);

        RecordedRequest recordedRequest = mServer.getRequest();
        assertThat(recordedRequest.getPath(), is("/graphql/"));
        assertThat(recordedRequest.getMethod(), is(POST.name()));

        verify(mListener).onSuccess(mFieldsResultCaptor.capture());
        verify(mListener, never()).onFailure(any(HyperwalletException.class));

        TransferMethodConfigurationResult transferMethodConfigurationResult = mFieldsResultCaptor.getValue();

        assertThat(transferMethodConfigurationResult.getFields().size(), is(2));

        HyperwalletField field1 = transferMethodConfigurationResult.getFields().get(0);
        assertThat(field1.getCategory(), is("ACCOUNT"));
        assertThat(field1.getDataType(), is(EDataType.getDataType("SELECTION")));
        assertThat(field1.isRequired(), is(false));
        assertThat(field1.getLabel(), is("Shipping Method"));
        assertThat(field1.getMinLength(), is(0));
        assertThat(field1.getMaxLength(), is(Integer.MAX_VALUE));
        assertThat(field1.getName(), is("shippingMethod"));
        assertThat(field1.getPlaceholder(), is(emptyString()));
        assertThat(field1.getRegularExpression(), is(emptyString()));

        HyperwalletField field2 = transferMethodConfigurationResult.getFields().get(1);
        assertThat(field2.getCategory(), is("ADDRESS"));
        assertThat(field2.getDataType(), is(EDataType.getDataType("SELECTION")));
        assertThat(field2.isRequired(), is(true));
        assertThat(field2.getLabel(), is("Country"));
        assertThat(field2.getMinLength(), is(2));
        assertThat(field2.getMaxLength(), is(30));
        assertThat(field2.getName(), is("country"));
        assertThat(field2.getPlaceholder(), is(emptyString()));
        assertThat(field2.getRegularExpression(), is(emptyString()));

    }

    @Test
    public void testRetrieveTransferMethodConfigurationFields_withErrorReturningFields()
            throws InterruptedException {
        String responseBody = mHyperwalletResourceManager.getResourceContentError(
                "gql_error_response.json");

        mServer.mockResponse().withHttpResponseCode(HttpURLConnection.HTTP_BAD_REQUEST).withBody(responseBody).mock();
        HyperwalletTransferMethodConfigurationFieldQuery transferMethodConfigurationResult =
                new HyperwalletTransferMethodConfigurationFieldQuery("US",
                        "USD",
                        "BANK_ACCOUNT",
                        "INDIVIDUAL");

        Hyperwallet.getDefault().retrieveTransferMethodConfigurationFields(transferMethodConfigurationResult,
                mListener);

        mAwait.await(500, TimeUnit.MILLISECONDS);
        RecordedRequest recordedRequest = mServer.getRequest();
        assertThat(recordedRequest.getPath(), is("/graphql/"));
        assertThat(recordedRequest.getMethod(), is(POST.name()));

        verify(mListener, never()).onSuccess(any(HyperwalletTransferMethodConfigurationFieldResult.class));
        verify(mListener).onFailure(mExceptionCaptor.capture());

        HyperwalletException hyperwalletException = mExceptionCaptor.getValue();
        assertThat(hyperwalletException, is(notNullValue()));
        HyperwalletErrors hyperwalletErrors = hyperwalletException.getHyperwalletErrors();
        assertThat(hyperwalletErrors, is(notNullValue()));
        assertThat(hyperwalletErrors.getErrors(), is(notNullValue()));
        assertThat(hyperwalletErrors.getErrors().size(), is(1));

        HyperwalletError hyperwalletError = hyperwalletErrors.getErrors().get(0);
        assertThat(hyperwalletError.getCode(), is("DataFetchingException"));
        assertThat(hyperwalletError.getMessage(), is("Could not find any currency."));
    }
}
