package pl.luxan.luxofttaskv4.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.luxan.luxofttaskv4.LuxoftTaskV4Application;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {LuxoftTaskV4Application.class})
public class ClientDataControllerIT {

    @Value("${api.path}")
    protected String apiPath;

    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    protected EntityManagerFactory entityManagerFactory;

    protected MockMvc mockMvc;

    protected static final String CSV_HEADER = """
            PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP""";
    protected static final String CSV_CONTENT = """
            CLIENT1,Jack Sparrow,Pirates of the Caribbean1,2003-01-01T11:22Z
            CLIENT2,Barbosa,Pirates of the Caribbean2,2004-02-02T22:33Z
            CLIENT3,Will Turner,Pirates of the Caribbean3,2005-03-03T13:44Z""";
    private static final String CSV_CONTENT_INVALID_TIMESTAMP = """
            CLIENT4,Pintel,Pirates of the Caribbean4,2005:03:03T13:44Z""";
    private static final String CSV_CONTENT_EMPTY_PRIMARY_KEY = """
            ,Pintel,Pirates of the Caribbean5,2005:03:03T14:44Z""";

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void cleanup() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("truncate table client_data").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }


    @Test
    public void itShouldNotFailIfCsvFileIsEmpty() throws Exception {
        MockMultipartFile csv = new MockMultipartFile("csvFile", "file.csv", "text/plain", "".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(apiPath + "/uploadCSV")
                        .file(csv))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNumberOfRows").value(0))
                .andExpect(jsonPath("$.primaryKeysUploaded", hasSize(0)));
    }

    @Test
    public void itShouldNotFailIfCsvFileLastLineIsEmpty() throws Exception {
        final String csvContent = CSV_HEADER + "\n" + CSV_CONTENT + "\n";
        MockMultipartFile csv = new MockMultipartFile("csvFile", "file.csv", "text/plain", csvContent.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(apiPath + "/uploadCSV")
                        .file(csv))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNumberOfRows").value(3))
                .andExpect(jsonPath("$.primaryKeysUploaded", hasSize(3)))
                .andExpect(jsonPath("$.primaryKeysUploaded[0]").value("CLIENT1"))
                .andExpect(jsonPath("$.primaryKeysUploaded[1]").value("CLIENT2"))
                .andExpect(jsonPath("$.primaryKeysUploaded[2]").value("CLIENT3"));
    }

    @Test
    public void itShouldNotSaveClientDataRowIfUpdatedTimestampIsNotIso8601() throws Exception {
        final String csvContent = CSV_HEADER + "\n" + CSV_CONTENT + "\n" + CSV_CONTENT_INVALID_TIMESTAMP;
        MockMultipartFile csv = new MockMultipartFile("csvFile", "file.csv", "text/plain", csvContent.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(apiPath + "/uploadCSV")
                        .file(csv))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNumberOfRows").value(3))
                .andExpect(jsonPath("$.primaryKeysUploaded", hasSize(3)))
                .andExpect(jsonPath("$.primaryKeysUploaded[0]").value("CLIENT1"))
                .andExpect(jsonPath("$.primaryKeysUploaded[1]").value("CLIENT2"))
                .andExpect(jsonPath("$.primaryKeysUploaded[2]").value("CLIENT3"));

        mockMvc.perform(get(apiPath + "/clientData/CLIENT4"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void itShouldNotFailIfCsvFileHasNoHeader() throws Exception {
        MockMultipartFile csv = new MockMultipartFile("csvFile", "file.csv", "text/plain", CSV_CONTENT.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(apiPath + "/uploadCSV")
                        .file(csv))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNumberOfRows").value(3))
                .andExpect(jsonPath("$.primaryKeysUploaded", hasSize(3)))
                .andExpect(jsonPath("$.primaryKeysUploaded[0]").value("CLIENT1"))
                .andExpect(jsonPath("$.primaryKeysUploaded[1]").value("CLIENT2"))
                .andExpect(jsonPath("$.primaryKeysUploaded[2]").value("CLIENT3"));
    }

    @Test
    public void itShouldIgnoreClientDataIfPrimaryKeyIsEmpty() throws Exception {
        final String csvContent = CSV_HEADER + "\n" + CSV_CONTENT + "\n" + CSV_CONTENT_EMPTY_PRIMARY_KEY;
        MockMultipartFile csv = new MockMultipartFile("csvFile", "file.csv", "text/plain", csvContent.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(apiPath + "/uploadCSV")
                        .file(csv))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNumberOfRows").value(3))
                .andExpect(jsonPath("$.primaryKeysUploaded", hasSize(3)))
                .andExpect(jsonPath("$.primaryKeysUploaded[0]").value("CLIENT1"))
                .andExpect(jsonPath("$.primaryKeysUploaded[1]").value("CLIENT2"))
                .andExpect(jsonPath("$.primaryKeysUploaded[2]").value("CLIENT3"));
    }

    @Test
    public void itShouldFailIfCsvFileHasRowsWithSamePrimaryKey() throws Exception {
        final String csvContent = CSV_HEADER + "\n" + CSV_CONTENT + "\n" + CSV_CONTENT;
        MockMultipartFile csv = new MockMultipartFile("csvFile", "file.csv", "text/plain", csvContent.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(apiPath + "/uploadCSV")
                        .file(csv))
                .andExpect(status().is(400));
    }

    @Test
    public void itShouldUploadClientDataCsvFile() throws Exception {
        final String csvContent = CSV_HEADER + "\n" + CSV_CONTENT;
        MockMultipartFile csv = new MockMultipartFile("csvFile", "file.csv", "text/plain", csvContent.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(apiPath + "/uploadCSV")
                        .file(csv))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNumberOfRows").value(3))
                .andExpect(jsonPath("$.primaryKeysUploaded", hasSize(3)))
                .andExpect(jsonPath("$.primaryKeysUploaded[0]").value("CLIENT1"))
                .andExpect(jsonPath("$.primaryKeysUploaded[1]").value("CLIENT2"))
                .andExpect(jsonPath("$.primaryKeysUploaded[2]").value("CLIENT3"));
    }

    @Test
    public void itShouldGetClientDataByPrimaryKey() throws Exception {
        final String csvContent = CSV_HEADER + "\n" + CSV_CONTENT;
        MockMultipartFile csv = new MockMultipartFile("csvFile", "file.csv", "text/plain", csvContent.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(apiPath + "/uploadCSV")
                        .file(csv))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNumberOfRows").value(3))
                .andExpect(jsonPath("$.primaryKeysUploaded", hasSize(3)))
                .andExpect(jsonPath("$.primaryKeysUploaded[0]").value("CLIENT1"))
                .andExpect(jsonPath("$.primaryKeysUploaded[1]").value("CLIENT2"))
                .andExpect(jsonPath("$.primaryKeysUploaded[2]").value("CLIENT3"));

        mockMvc.perform(get(apiPath + "/clientData/CLIENT1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryKey").value("CLIENT1"))
                .andExpect(jsonPath("$.name").value("Jack Sparrow"))
                .andExpect(jsonPath("$.description").value("Pirates of the Caribbean1"))
                .andExpect(jsonPath("$.updatedTimestamp").value("2003-01-01T11:22Z"));
        mockMvc.perform(get(apiPath + "/clientData/CLIENT2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryKey").value("CLIENT2"))
                .andExpect(jsonPath("$.name").value("Barbosa"))
                .andExpect(jsonPath("$.description").value("Pirates of the Caribbean2"))
                .andExpect(jsonPath("$.updatedTimestamp").value("2004-02-02T22:33Z"));
        mockMvc.perform(get(apiPath + "/clientData/CLIENT3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryKey").value("CLIENT3"))
                .andExpect(jsonPath("$.name").value("Will Turner"))
                .andExpect(jsonPath("$.description").value("Pirates of the Caribbean3"))
                .andExpect(jsonPath("$.updatedTimestamp").value("2005-03-03T13:44Z"));
    }

    @Test
    public void whenGetClientDataByPrimaryKeyItShouldReturn404WhenClientDataNotExist() throws Exception {
        mockMvc.perform(get(apiPath + "/clientData/CLIENT4"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void itShouldDeleteClientDataByPrimaryKey() throws Exception {
        final String csvContent = CSV_HEADER + "\n" + CSV_CONTENT;
        MockMultipartFile csv = new MockMultipartFile("csvFile", "file.csv", "text/plain", csvContent.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(apiPath + "/uploadCSV")
                        .file(csv))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNumberOfRows").value(3))
                .andExpect(jsonPath("$.primaryKeysUploaded", hasSize(3)))
                .andExpect(jsonPath("$.primaryKeysUploaded[0]").value("CLIENT1"))
                .andExpect(jsonPath("$.primaryKeysUploaded[1]").value("CLIENT2"))
                .andExpect(jsonPath("$.primaryKeysUploaded[2]").value("CLIENT3"));

        mockMvc.perform(delete(apiPath + "/clientData/CLIENT1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryKey").value("CLIENT1"));

        mockMvc.perform(get(apiPath + "/clientData/CLIENT1"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(apiPath + "/clientData/CLIENT2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryKey").value("CLIENT2"))
                .andExpect(jsonPath("$.name").value("Barbosa"))
                .andExpect(jsonPath("$.description").value("Pirates of the Caribbean2"))
                .andExpect(jsonPath("$.updatedTimestamp").value("2004-02-02T22:33Z"));
        mockMvc.perform(get(apiPath + "/clientData/CLIENT3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryKey").value("CLIENT3"))
                .andExpect(jsonPath("$.name").value("Will Turner"))
                .andExpect(jsonPath("$.description").value("Pirates of the Caribbean3"))
                .andExpect(jsonPath("$.updatedTimestamp").value("2005-03-03T13:44Z"));
    }

    @Test
    public void whenDeleteClientDataByPrimaryKeyItShouldReturn404WhenClientDataNotExist() throws Exception {
        mockMvc.perform(delete(apiPath + "/clientData/CLIENT1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void itShouldOverrideClientDataIfClientDataWithPrimaryKeyExist() throws Exception {
        String csvContent = CSV_HEADER + "\n" + CSV_CONTENT;
        MockMultipartFile csv = new MockMultipartFile("csvFile", "file.csv", "text/plain", csvContent.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(apiPath + "/uploadCSV")
                        .file(csv))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNumberOfRows").value(3))
                .andExpect(jsonPath("$.primaryKeysUploaded", hasSize(3)))
                .andExpect(jsonPath("$.primaryKeysUploaded[0]").value("CLIENT1"))
                .andExpect(jsonPath("$.primaryKeysUploaded[1]").value("CLIENT2"))
                .andExpect(jsonPath("$.primaryKeysUploaded[2]").value("CLIENT3"));

        mockMvc.perform(get(apiPath + "/clientData/CLIENT1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryKey").value("CLIENT1"))
                .andExpect(jsonPath("$.name").value("Jack Sparrow"))
                .andExpect(jsonPath("$.description").value("Pirates of the Caribbean1"))
                .andExpect(jsonPath("$.updatedTimestamp").value("2003-01-01T11:22Z"));

        csvContent = CSV_HEADER + "\n" + """
                CLIENT1,Jack Sparrow reborn,Pirates of the Caribbean5,2010-01-01T11:22Z""";
        csv = new MockMultipartFile("csvFile", "file.csv", "text/plain", csvContent.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(apiPath + "/uploadCSV")
                        .file(csv))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNumberOfRows").value(1))
                .andExpect(jsonPath("$.primaryKeysUploaded", hasSize(1)))
                .andExpect(jsonPath("$.primaryKeysUploaded[0]").value("CLIENT1"));

        mockMvc.perform(get(apiPath + "/clientData/CLIENT1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryKey").value("CLIENT1"))
                .andExpect(jsonPath("$.name").value("Jack Sparrow reborn"))
                .andExpect(jsonPath("$.description").value("Pirates of the Caribbean5"))
                .andExpect(jsonPath("$.updatedTimestamp").value("2010-01-01T11:22Z"));
    }

}
