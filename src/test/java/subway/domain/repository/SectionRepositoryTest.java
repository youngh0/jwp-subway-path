package subway.domain.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import subway.persistence.repository.SectionRepositoryImpl;
import subway.service.line.LineRepository;
import subway.service.line.domain.Line;
import subway.service.section.domain.Distance;
import subway.service.section.domain.Section;
import subway.service.section.domain.Sections;
import subway.service.station.StationRepository;
import subway.service.station.domain.Station;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static subway.domain.LineFixture.EIGHT_LINE_NO_ID;
import static subway.domain.LineFixture.SECOND_LINE_NO_ID;
import static subway.domain.StationFixture.GANGNAM_NO_ID;
import static subway.domain.StationFixture.JAMSIL_NO_ID;
import static subway.domain.StationFixture.SEOKCHON;
import static subway.domain.StationFixture.SEONLEUNG_NO_ID;
import static subway.domain.StationFixture.YUKSAM_NO_ID;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
@Transactional
@Sql("/test-schema.sql")
class SectionRepositoryTest {

    @Autowired
    SectionRepositoryImpl sectionRepositoryImpl;
    @Autowired
    StationRepository stationRepository;

    @Autowired
    LineRepository lineRepository;

    @Test
    void 섹션_추가() {
        Station savedJamsil = stationRepository.insert(JAMSIL_NO_ID);
        Station savedSeonleung = stationRepository.insert(SEONLEUNG_NO_ID);

        Line savedSecondLine = lineRepository.insert(SECOND_LINE_NO_ID);


        Distance seonleungJamsilDistance = new Distance(10);
        Section seonleungJamsilSection = new Section(savedJamsil, savedSeonleung, seonleungJamsilDistance);

        Section savedSection = sectionRepositoryImpl.insertSection(seonleungJamsilSection, savedSecondLine);
        assertAll(
                () -> assertThat(savedSection.getId()).isPositive(),
                () -> assertThat(savedSection.getUpStation()).isEqualTo(savedJamsil),
                () -> assertThat(savedSection.getDownStation()).isEqualTo(savedSeonleung),
                () -> assertThat(savedSection.getDistance()).isEqualTo(seonleungJamsilDistance)
        );
    }

    @Test
    void 섹션_조회() {
        Station savedJamsil = stationRepository.insert(JAMSIL_NO_ID);
        Station savedSeonleung = stationRepository.insert(SEONLEUNG_NO_ID);
        Station savedGangnam = stationRepository.insert(GANGNAM_NO_ID);

        Line savedSecondLine = lineRepository.insert(SECOND_LINE_NO_ID);

        Section jamsilToSeonleung = new Section(savedJamsil, savedSeonleung, new Distance(10));
        Section gangnamToSeonleung = new Section(savedSeonleung, savedGangnam, new Distance(3));

        sectionRepositoryImpl.insertSection(jamsilToSeonleung, savedSecondLine);
        sectionRepositoryImpl.insertSection(gangnamToSeonleung, savedSecondLine);

        Sections sectionsByLine = sectionRepositoryImpl.findSectionsByLine(savedSecondLine);

        Section findSeonleungJamsilSection = sectionsByLine.getSections().stream()
                .filter(section -> section.contains(savedJamsil) && section.contains(savedSeonleung))
                .findAny().get();

        Section findSeonleungGangnamSection = sectionsByLine.getSections().stream()
                .filter(section -> section.contains(savedSeonleung) && section.contains(savedGangnam))
                .findAny().get();


        assertAll(
                () -> assertThat(sectionsByLine.getSections()).hasSize(2),
                () -> assertThat(findSeonleungJamsilSection.getDistance()).isEqualTo(new Distance(10)),
                () -> assertThat(findSeonleungGangnamSection.getDistance()).isEqualTo(new Distance(3))
        );

    }

    @Test
    void 섹션_삭제() {
        // given
        Station savedJamsil = stationRepository.insert(JAMSIL_NO_ID);
        Station savedSeonleung = stationRepository.insert(SEONLEUNG_NO_ID);
        Station savedGangnam = stationRepository.insert(GANGNAM_NO_ID);

        Line savedSecondLine = lineRepository.insert(SECOND_LINE_NO_ID);
        Line savedEightLine = lineRepository.insert(EIGHT_LINE_NO_ID);

        Section seonleungToJamsilSection = new Section(savedJamsil, savedSeonleung, new Distance(10));
        Section gangnamToSeonleungSection = new Section(savedSeonleung, savedGangnam, new Distance(5));

        Section eightLineSection = new Section(savedJamsil, savedGangnam, new Distance(7));


        Section savedSeonleungToJamsilSection = sectionRepositoryImpl.insertSection(seonleungToJamsilSection, savedSecondLine);
        Section savedGangnamToSeonleungSection = sectionRepositoryImpl.insertSection(gangnamToSeonleungSection, savedSecondLine);
        Section savedSectionInEightLine = sectionRepositoryImpl.insertSection(eightLineSection, savedEightLine);

        //when
        sectionRepositoryImpl.deleteSection(savedSeonleungToJamsilSection);

        Sections sectionsByLine = sectionRepositoryImpl.findSectionsByLine(savedSecondLine);

        //then
        assertAll(
                () -> assertThat(sectionsByLine.getSections()).doesNotContain(savedSectionInEightLine),
                () -> assertThat(sectionsByLine.getSections()).doesNotContain(savedSeonleungToJamsilSection),
                () -> assertThat(sectionsByLine.getSections()).contains(savedGangnamToSeonleungSection),
                () -> assertThat(sectionsByLine.getSections()).hasSize(1)
        );

    }

    @Test
    void 호선_상관없이_지하철_역과_연결된_역_조회() {
        // given
        Station savedJamsil = stationRepository.insert(JAMSIL_NO_ID);
        Station savedSeonleung = stationRepository.insert(SEONLEUNG_NO_ID);
        Station savedGangnam = stationRepository.insert(GANGNAM_NO_ID);
        Station savedSeokchon = stationRepository.insert(SEOKCHON);

        Line savedSecondLine = lineRepository.insert(SECOND_LINE_NO_ID);
        Line savedEightLine = lineRepository.insert(EIGHT_LINE_NO_ID);

        Section seonleungToJamsilSection = new Section(savedJamsil, savedSeonleung, new Distance(10));
        Section gangnamToSeonleungSection = new Section(savedSeonleung, savedGangnam, new Distance(5));

        Section seokchonToJamsil = new Section(savedJamsil, savedSeokchon, new Distance(7));

        sectionRepositoryImpl.insertSection(seonleungToJamsilSection, savedSecondLine);
        sectionRepositoryImpl.insertSection(seokchonToJamsil, savedEightLine);
        sectionRepositoryImpl.insertSection(gangnamToSeonleungSection, savedSecondLine);

        // when
        Map<Line, Sections> sectionsPerLine = sectionRepositoryImpl.findSectionsByStation(savedJamsil);

        // 2호선 역 아이디 추출
        Sections secondSections = sectionsPerLine.get(savedSecondLine);

        List<Long> secondUpStations = secondSections.getSections().stream()
                .map(section -> section.getUpStation().getId())
                .collect(Collectors.toList());

        List<Long> secondDownStations = secondSections.getSections().stream()
                .map(section -> section.getDownStation().getId())
                .collect(Collectors.toList());

        secondUpStations.addAll(secondDownStations);
        List<Long> secondDistinctIds = secondUpStations.stream()
                .distinct()
                .collect(Collectors.toList());

        // 8호선 역 아이디 추출
        Sections eightSections = sectionsPerLine.get(savedEightLine);
        List<Long> eightUpStations = eightSections.getSections().stream()
                .map(section -> section.getUpStation().getId())
                .collect(Collectors.toList());

        List<Long> eightDownStations = eightSections.getSections().stream()
                .map(section -> section.getDownStation().getId())
                .collect(Collectors.toList());

        eightUpStations.addAll(eightDownStations);
        List<Long> eightDistinctIds = eightUpStations.stream()
                .distinct()
                .collect(Collectors.toList());

        // then
        assertAll(
                () -> assertThat(sectionsPerLine.keySet()).containsExactlyInAnyOrder(savedEightLine, savedSecondLine),
                () -> assertThat(secondSections.getSections()).hasSize(1),
                () -> assertThat(secondDistinctIds).containsExactlyInAnyOrder(savedJamsil.getId(), savedSeonleung.getId()),
                () -> assertThat(eightDistinctIds).containsExactlyInAnyOrder(savedJamsil.getId(), savedSeokchon.getId())
        );
    }

    @Test
    void 해당_라인의_구간이_하나_남아있으면_true() {
        Station savedJamsil = stationRepository.insert(JAMSIL_NO_ID);
        Station savedSeonleung = stationRepository.insert(SEONLEUNG_NO_ID);

        Line savedSecondLine = lineRepository.insert(SECOND_LINE_NO_ID);

        Distance seonleungJamsilDistance = new Distance(10);
        Section seonleungJamsilSection = new Section(savedJamsil, savedSeonleung, seonleungJamsilDistance);

        sectionRepositoryImpl.insertSection(seonleungJamsilSection, savedSecondLine);
        assertThat(sectionRepositoryImpl.isLastSectionInLine(savedSecondLine)).isTrue();
    }

    @Test
    void 해당_라인의_구간이_둘_이상이면_false() {
        Station savedJamsil = stationRepository.insert(JAMSIL_NO_ID);
        Station savedSeonleung = stationRepository.insert(SEONLEUNG_NO_ID);
        Station savedYuksam = stationRepository.insert(YUKSAM_NO_ID);

        Line savedSecondLine = lineRepository.insert(SECOND_LINE_NO_ID);

        Distance seonleungJamsilDistance = new Distance(10);
        Section seonleungJamsilSection = new Section(savedJamsil, savedSeonleung, seonleungJamsilDistance);
        Section seonleungYuksamSection = new Section(savedYuksam, savedSeonleung, new Distance(3));

        sectionRepositoryImpl.insertSection(seonleungJamsilSection, savedSecondLine);
        sectionRepositoryImpl.insertSection(seonleungYuksamSection, savedSecondLine);
        assertThat(sectionRepositoryImpl.isLastSectionInLine(savedSecondLine)).isFalse();
    }
}
