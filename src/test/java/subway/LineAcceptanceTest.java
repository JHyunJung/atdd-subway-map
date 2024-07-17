package subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import subway.line.LineResponse;


import java.util.HashMap;
import java.util.List;
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

    //Given 여러 개의 지하철 노선이 등록되어 있고,
    //When 관리자가 지하철 노선 목록을 조회하면,
    //Then 모든 지하철 노선 목록이 반환된다.
    @DisplayName("지하철 노선 목록 조회")
    @Test
    public void findAllLineTest() {
        //Given 여러 개의 지하철 노선이 등록되어 있고,
        Station station1 = createStation("강남역");
        Station station2 = createStation("역삼역");
        Station station3 = createStation("양재역");

        String lineName1 = "2호선";
        String lineColor1 = "bg-green-700";

        String lineName2 = "신분당선";
        String lineColor2 = "bg-red-600";

        int lineDistance1 = 20;
        int lineDistance2 = 10;

        Long station1Id = station1.getId();
        Long station2Id = station2.getId();
        Long station3Id = station3.getId();

        createLine(lineName1, lineColor1, station1Id, station2Id, lineDistance1);
        createLine(lineName2, lineColor2, station1Id, station3Id, lineDistance2);

        //When 관리자가 지하철 노선 목록을 조회하면,
        ExtractableResponse<Response> response = findAllLines();


        //Then 모든 지하철 노선 목록이 반환된다.
        List<LineResponse> lines = response.jsonPath().getList(".", LineResponse.class);
        assertThat(lines).hasSize(2);
        assertThat(lines.get(0).getName()).isEqualTo("2호선");
        assertThat(lines.get(1).getName()).isEqualTo("신분당선");
        assertThat(lines.get(0).getStations().get(1).getId()).isEqualTo(station2.getId());
        assertThat(lines.get(1).getStations().get(1).getId()).isEqualTo(station3.getId());
    }

    //Given 특정 지하철 노선이 등록되어 있고,
    //When 관리자가 해당 노선을 조회하면,
    //Then 해당 노선의 정보가 반환된다.
    @DisplayName("지하철 노선 조회")
    @Test
    public void findLineTest() {
        //Given 특정 지하철 노선이 등록되어 있고,
        Station station1 = createStation("강남역");
        Station station2 = createStation("역삼역");
        Station station3 = createStation("양재역");

        String lineName1 = "2호선";
        String lineColor1 = "bg-green-700";

        String lineName2 = "신분당선";
        String lineColor2 = "bg-red-600";

        int lineDistance1 = 20;
        int lineDistance2 = 10;

        Long station1Id = station1.getId();
        Long station2Id = station2.getId();
        Long station3Id = station3.getId();

        createLine(lineName1, lineColor1, station1Id, station2Id, lineDistance1);
        createLine(lineName2, lineColor2, station1Id, station3Id, lineDistance2);
        //When 관리자가 해당 노선을 조회하면,
        ExtractableResponse<Response> response = findLine(1L);

        //Then 해당 노선의 정보가 반환된다.
        assertThat(getResponse(response, "stations")).contains("강남역");
        assertThat(getResponse(response, "stations")).contains("역삼역");
        assertThat(getResponse(response, "name")).isEqualTo(lineName1);
        assertThat(getResponse(response, "color")).isEqualTo(lineColor1);
    }

    //Given 특정 지하철 노선이 등록되어 있고,
    //When 관리자가 해당 노선을 수정하면,
    //Then 해당 노선의 정보가 수정된다.
    @DisplayName("지하철 노선 수정")
    @Test
    public void updateLineTest() {
        //Given 특정 지하철 노선이 등록되어 있고,
        Station upStation = createStation("강남역");
        Station downStation = createStation("역삼역");
        String name = "신분당선";
        String color = "bg-red-600";
        Long upStationId = upStation.getId();
        Long downStationId = downStation.getId();
        int distnace = 10;
        createLine(name, color, upStationId, downStationId, distnace);

        //When 관리자가 해당 노선을 수정하면,
        ExtractableResponse<Response> response = updateLine(1L);

        //Then 해당 노선의 정보가 수정된다.
        assertThat(response.statusCode()).isEqualTo(200);
    }

    //Given 특정 지하철 노선이 등록되어 있고,
    //When 관리자가 해당 노선을 삭제하면,
    //Then 해당 노선이 삭제되고 노선 목록에서 제외된다.
    @DisplayName("지하철 노선 삭제")
    @Test
    public void deleteLineTest() {
        //Given 특정 지하철 노선이 등록되어 있고,
        Station upStation = createStation("강남역");
        Station downStation = createStation("역삼역");
        String name = "신분당선";
        String color = "bg-red-600";
        Long upStationId = upStation.getId();
        Long downStationId = downStation.getId();
        int distnace = 10;
        createLine(name, color, upStationId, downStationId, distnace);

        //When 관리자가 해당 노선을 삭제하면,
        ExtractableResponse<Response> response = deleteLine(1L);

        //Then 해당 노선이 삭제되고 노선 목록에서 제외된다.
        ExtractableResponse<Response> response2 = findAllLines();
        List<LineResponse> lines = response2.jsonPath().getList(".", LineResponse.class);
        assertThat(lines).hasSize(0);
    }

    private ExtractableResponse<Response> deleteLine(Long id) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> updateLine(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "3호선");
        params.put("color", "bg-blue-600");

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findLine(Long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findAllLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
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
