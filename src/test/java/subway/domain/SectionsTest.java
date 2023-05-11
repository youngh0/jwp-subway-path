package subway.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import subway.dto.AddResultDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static subway.domain.LineFixture.SECOND_LINE;
import static subway.domain.StationFixture.*;

@SuppressWarnings("NonAsciiCharacters")
class SectionsTest {
    @Test
    void 동일한_역으로_추가하려고_하면_예외() {
        Sections sections = new Sections(new ArrayList<>());
        Distance distance = new Distance(10);
        assertThatThrownBy(() -> sections.add(JAMSIL, JAMSIL, distance, SECOND_LINE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("동일한 역 2개가 입력으로 들어왔습니다. 이름을 다르게 설정해주세요.");
    }

    @Test
    void 노선에_역이_없을_때_새로운_역을_추가한다() {
        //given
        Sections sections = new Sections(new ArrayList<>());
        Distance distance = new Distance(10);

        //when
        AddResultDto addResult = sections.add(StationFixture.JAMSIL, StationFixture.SEONLEUNG, distance, LineFixture.SECOND_LINE);

        //then
        List<Section> addedResults = addResult.getAddedResults();
        List<Section> deletedResults = addResult.getDeletedResults();
        List<Station> addedStation = addResult.getAddedStation();
        Section newSection = addedResults.get(0);

        Assertions.assertAll(
                () -> assertThat(addedResults).hasSize(1),
                () -> assertThat(newSection.getLine()).isEqualTo(SECOND_LINE),
                () -> assertThat(newSection.getUpStation()).isEqualTo(JAMSIL),
                () -> assertThat(newSection.getDownStation()).isEqualTo(SEONLEUNG),
                () -> assertThat(newSection.getDistance()).isEqualTo(distance),
                () -> assertThat(deletedResults).hasSize(0),
                () -> assertThat(addedStation).hasSize(2),
                () -> assertThat(addedStation).containsExactlyInAnyOrder(JAMSIL, SEONLEUNG)
        );
    }

    @Test
    void 노선에_역이_존재할_때_새로운_역_2개를_추가하려고_하면_예외() {
        Section section = new Section(JAMSIL, SEONLEUNG, new Distance(10), SECOND_LINE);
        Sections sections = new Sections(List.of(section));

        assertThatThrownBy(() -> sections.add(GANGNAM, YUKSAM, new Distance(5), SECOND_LINE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 노선에 역들이 존재하기 때문에 한 번에 새로운 역 2개를 추가할 수 없습니다.");
    }

    @Test
    void 상행_종점을_추가한다() {
        //given
        Distance gandnamYuksamDistance = new Distance(10);
        Section section = new Section(YUKSAM, GANGNAM, gandnamYuksamDistance, SECOND_LINE);
        Sections sections = new Sections(List.of(section));

        Distance jamsilYuksamDistance = new Distance(5);

        //when
        AddResultDto addUpEndStationResult = sections.add(JAMSIL, YUKSAM, jamsilYuksamDistance, SECOND_LINE);

        //then
        List<Section> addedResults = addUpEndStationResult.getAddedResults();
        List<Section> deletedResults = addUpEndStationResult.getDeletedResults();
        List<Station> addedStation = addUpEndStationResult.getAddedStation();

        Section newSection = addedResults.get(0);

        Assertions.assertAll(
                () -> assertThat(addedResults).hasSize(1),
                () -> assertThat(newSection.getLine()).isEqualTo(SECOND_LINE),
                () -> assertThat(newSection.getUpStation()).isEqualTo(JAMSIL),
                () -> assertThat(newSection.getDownStation()).isEqualTo(YUKSAM),
                () -> assertThat(newSection.getDistance()).isEqualTo(jamsilYuksamDistance),
                () -> assertThat(deletedResults).hasSize(0),
                () -> assertThat(addedStation).hasSize(1),
                () -> assertThat(addedStation).containsExactly(JAMSIL)
        );
    }

    @Test
    void 하행_종점을_추가한다() {
        Distance jamsilGangnamDistance = new Distance(10);
        Section section = new Section(JAMSIL, GANGNAM, jamsilGangnamDistance, SECOND_LINE);
        Sections sections = new Sections(List.of(section));

        Distance gangnamYuksamDistance = new Distance(4);

        AddResultDto addDownEndStation = sections.add(GANGNAM, YUKSAM, gangnamYuksamDistance, SECOND_LINE);

        List<Section> addedResults = addDownEndStation.getAddedResults();
        List<Section> deletedResults = addDownEndStation.getDeletedResults();
        List<Station> addedStation = addDownEndStation.getAddedStation();

        Section newSection = addedResults.get(0);

        Assertions.assertAll(
                () -> assertThat(addedResults).hasSize(1),
                () -> assertThat(newSection.getLine()).isEqualTo(SECOND_LINE),
                () -> assertThat(newSection.getUpStation()).isEqualTo(GANGNAM),
                () -> assertThat(newSection.getDownStation()).isEqualTo(YUKSAM),
                () -> assertThat(newSection.getDistance()).isEqualTo(gangnamYuksamDistance),
                () -> assertThat(deletedResults).hasSize(0),
                () -> assertThat(addedStation).hasSize(1),
                () -> assertThat(addedStation).containsExactly(YUKSAM)
        );
    }

    @Test
    void 상행역이_존재하고_새로운_하행역을_추가한다() {
        // given
        Distance jamsilGangnamDistance = new Distance(10);
        Section jamsilGangnamSection = new Section(JAMSIL, GANGNAM, jamsilGangnamDistance, SECOND_LINE);
        Sections sections = new Sections(List.of(jamsilGangnamSection));

        Distance jamsilSeonleungDistance = new Distance(4);

        // when
        AddResultDto addDownwardInMiddleResult = sections.add(JAMSIL, SEONLEUNG, jamsilSeonleungDistance, SECOND_LINE);

        List<Section> addedResults = addDownwardInMiddleResult.getAddedResults();
        List<Section> deletedResults = addDownwardInMiddleResult.getDeletedResults();
        List<Station> addedStation = addDownwardInMiddleResult.getAddedStation();

        // 상행 역 조회
        List<Station> upStations = addedResults.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        // 하행 역 조회
        List<Station> downStations = addedResults.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        // 상행 역과 하행 역을 합쳐 노선의 역들 추출
        upStations.addAll(downStations);
        List<Station> stations = upStations.stream()
                .distinct()
                .collect(Collectors.toList());
        Assertions.assertAll(
                () -> assertThat(addedResults).hasSize(2),
                () -> assertThat(deletedResults).hasSize(1),
                () -> assertThat(addedStation).hasSize(1),
                () -> assertThat(addedStation).containsExactly(SEONLEUNG),
                () -> assertThat(stations).containsExactlyInAnyOrder(JAMSIL, SEONLEUNG, GANGNAM)
        );
    }
}
