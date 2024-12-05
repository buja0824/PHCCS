package PHCCS.service.vet_list;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerTest {

    @Autowired private MockMvc mockMvc;

    @Test
    @DisplayName("게시글 불러오기 테스트")
    public void testPostsShow() throws Exception {
        // given
        String uri = "/board/show/qna_board?searchName=문게시&page=1&size=150";
        // when

        MvcResult result = perFormGet(uri);
        String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

        JSONArray jsonArray = new JSONArray(contentAsString);

        log.info("+++++++++++");
        log.info("josnArr = {}", jsonArray);
        log.info("+++++++++++");
        // then
        assertThat(jsonArray.length()).isPositive();


    }
    public MvcResult perFormGet(String uri) throws Exception {
        return mockMvc
                .perform(MockMvcRequestBuilders.get(uri))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}