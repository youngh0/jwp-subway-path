package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.persistence.dao.LineDao;
import subway.persistence.dao.SectionDao;
import subway.persistence.dao.StationDao;
import subway.persistence.dao.entity.SectionEntity;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.domain.LineFixture.SECOND_LINE;
import static subway.domain.StationFixture.*;

@SuppressWarnings("NonAsciiCharacters")
@JdbcTest
class SectionDaoTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;
    private LineDao lineDao;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate, dataSource);
        lineDao = new LineDao(jdbcTemplate, dataSource);
        stationDao = new StationDao(jdbcTemplate, dataSource);
    }

    @Test
    void 새로운_섹션을_저장한다() {
        Station savedJamsil = stationDao.insert(JAMSIL);
        Station savedSeonleung = stationDao.insert(SEONLEUNG);

        Line savedSecondLine = lineDao.insert(SECOND_LINE);

        Section section = new Section(savedJamsil, savedSeonleung, new Distance(10));

        SectionEntity sectionEntity = new SectionEntity(savedJamsil.getId(), savedSeonleung.getId(), 10, savedSecondLine.getId());

        SectionEntity savedSection = sectionDao.insert(sectionEntity, savedSecondLine.getId());

        org.junit.jupiter.api.Assertions.assertAll(
                () -> assertThat(savedSection.getId()).isPositive(),
                () -> assertThat(savedSection.getUpStationId()).isEqualTo(savedJamsil.getId()),
                () -> assertThat(savedSection.getDownStationId()).isEqualTo(savedSeonleung.getId()),
                () -> assertThat(savedSection.getDistance()).isEqualTo(10)
        );
    }

    @Test
    void 섹션_조회() {
        Station savedJamsil = stationDao.insert(JAMSIL);
        Station savedSeonleung = stationDao.insert(SEONLEUNG);
        Station savedGangnam = stationDao.insert(GANGNAM);

        Line savedSecondLine = lineDao.insert(SECOND_LINE);

        Section upSection = new Section(savedJamsil, savedSeonleung, new Distance(10));
        Section downSection = new Section(savedSeonleung, savedGangnam, new Distance(3));

        SectionEntity upSectionEntity = new SectionEntity(savedJamsil.getId(), savedSeonleung.getId(), 10, savedSecondLine.getId());
        SectionEntity downSectionEntity = new SectionEntity(savedSeonleung.getId(), savedGangnam.getId(), 3, savedSecondLine.getId());

        sectionDao.insert(upSectionEntity, savedSecondLine.getId());
        sectionDao.insert(downSectionEntity, savedSecondLine.getId());


        sectionDao.findSectionsByLine(savedSecondLine.getId());
    }
}
