package subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;


import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {


    //Given 새로운 지하철 노선 정보를 입력하고
    //When 관리자가 노선을 생성하면
    //Then 해당 노선이 생성되고 노선 목록에 포함된다.
    @DisplayName("지하철 노선 생성")
    @Test
    public void createLineTest() {
        //Given 새로운 지하철 노선 정보를 입력하고
        Station upStation = createStation("강남역");
        Station downStation = createStation("역삼역");
        String name = "신분당선";
        String color = "bg-red-600";
        Long upStationId = upStation.getId();
        Long downStationId = downStation.getId();
        int distnace = 10;

        //When 관리자가 노선을 생성하면
        ExtractableResponse<Response> response = createLine(name, color, upStationId, downStationId, distnace);

        //Then 해당 노선이 생성되고 노선 목록에 포함된다.
        assertThat(getResponse(response, "stations")).contains("강남역");
        assertThat(getResponse(response, "stations")).contains("역삼역");
        assertThat(getResponse(response, "name")).isEqualTo(name);
        assertThat(getResponse(response, "color")).isEqualTo(color);
    }

    private ExtractableResponse<Response> createLine(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private String getResponse(ExtractableResponse<Response> response, String key) {
        return response.body().jsonPath().getString(key);
    }

    private Station createStation(String name) {
        ExtractableResponse<Response> response = createStationAPI(name);
        Long id = response.body().jsonPath().getLong("id");
        String stationName = response.body().jsonPath().getString("name");
        return new Station(id, stationName);
    }

    private ExtractableResponse<Response> createStationAPI(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }
}
