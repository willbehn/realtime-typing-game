package no.behn.typingStomp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    //Test to check if the post request return all the expected values after creating a new room
    @Test
    public void testCreateRoom() throws Exception {
        
        ResultActions result = mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        result.andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.clientPositions").isMap())
            .andExpect(jsonPath("$.clientEndtimes").exists())
            .andExpect(jsonPath("$.text").exists())
            .andExpect(jsonPath("$.startTime").exists())
            .andExpect(jsonPath("$.state").isBoolean())
            .andExpect(jsonPath("$.done").isBoolean());

        result.andExpect(jsonPath("$.state").value(false))
            .andExpect(jsonPath("$.done").value(false));
    }

    //Test to check if controller returns status not found when trying to join a room that doesnt exist
    @Test
    public void testJoinRoomThatDontExist() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/rooms/123/join")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

     @Test
    public void testJoinRoomThatExist() throws Exception {
        //Creates room
        MvcResult createRoomResult = mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();


        String jsonResponse = createRoomResult.getResponse().getContentAsString();

        //Gets room id from response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String roomId = jsonNode.get("id").asText();
    
        mockMvc.perform(post("/api/rooms/" + roomId + "/join")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());        
    }
}

