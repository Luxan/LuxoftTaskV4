package pl.luxan.luxofttaskv4.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import pl.luxan.luxofttaskv4.model.ClientData;
import pl.luxan.luxofttaskv4.repository.ClientDataRepository;
import pl.luxan.luxofttaskv4.service.dto.ClientDataDTO;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientDataServiceUT {

    public static final String TEST_PRIMARY_KEY = "TEST_PRIMARY_KEY";
    public static final String INVALID_PRIMARY_KEY = "INVALID_PRIMARY_KEY";

    @Mock
    ClientDataRepository clientDataRepositoryMock;

    ClientDataService testSubject;
    @Before
    public void setUp() {
        testSubject = new ClientDataService(clientDataRepositoryMock);
    }

    @Test
    public void getClientDetailsByPrimaryKey() {
        ClientData clientData = ClientData.builder().primaryKey(TEST_PRIMARY_KEY).build();
        Optional<ClientData> optionalClientData = Optional.of(clientData);

        when(clientDataRepositoryMock.findByPrimaryKey(TEST_PRIMARY_KEY)).thenReturn(optionalClientData);
        when(clientDataRepositoryMock.findByPrimaryKey(INVALID_PRIMARY_KEY)).thenReturn(Optional.empty());

        Optional<ClientDataDTO> resultOptional = testSubject.getClientDetailsByPrimaryKey(TEST_PRIMARY_KEY);

        assertThat(resultOptional).isPresent();
        assertThat(resultOptional.get().getPrimaryKey()).isEqualTo(TEST_PRIMARY_KEY);
        verify(clientDataRepositoryMock, times(1)).findByPrimaryKey(TEST_PRIMARY_KEY);

        resultOptional = testSubject.getClientDetailsByPrimaryKey(INVALID_PRIMARY_KEY);
        assertThat(resultOptional).isEmpty();
        verify(clientDataRepositoryMock, times(1)).findByPrimaryKey(INVALID_PRIMARY_KEY);
    }

    @Test
    public void saveOrUpdateClientData() {
        ClientDataDTO clientDataDTO = ClientDataDTO.builder().primaryKey(TEST_PRIMARY_KEY).build();

        doAnswer(invocation -> invocation.getArguments()[0]).when(clientDataRepositoryMock).save(any());

        List<ClientDataDTO> returnedClientData = testSubject.saveClientData(Collections.singletonList(clientDataDTO));
        assertThat(returnedClientData.size()).isEqualTo(1);
        assertThat(returnedClientData.get(0).getPrimaryKey()).isEqualTo(TEST_PRIMARY_KEY);
        verify(clientDataRepositoryMock, times(1)).save(any());
    }

    @Test
    public void deleteClientData() {
        ClientData clientData = ClientData.builder().primaryKey(TEST_PRIMARY_KEY).build();
        Optional<ClientData> optionalClientData = Optional.of(clientData);

        when(clientDataRepositoryMock.findByPrimaryKey(TEST_PRIMARY_KEY)).thenReturn(optionalClientData);
        when(clientDataRepositoryMock.findByPrimaryKey(INVALID_PRIMARY_KEY)).thenReturn(Optional.empty());

        Optional<ClientDataDTO> resultOptional = testSubject.deleteClientData(TEST_PRIMARY_KEY);

        assertThat(resultOptional).isPresent();
        assertThat(resultOptional.get().getPrimaryKey()).isEqualTo(TEST_PRIMARY_KEY);
        verify(clientDataRepositoryMock, times(1)).findByPrimaryKey(TEST_PRIMARY_KEY);
        verify(clientDataRepositoryMock, times(1)).delete(any());

        resultOptional = testSubject.deleteClientData(INVALID_PRIMARY_KEY);
        assertThat(resultOptional).isEmpty();
        verify(clientDataRepositoryMock, times(1)).findByPrimaryKey(INVALID_PRIMARY_KEY);
        verify(clientDataRepositoryMock, times(1)).delete(any());
    }
}