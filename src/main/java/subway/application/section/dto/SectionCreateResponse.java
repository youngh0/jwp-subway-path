package subway.application.section.dto;

import subway.domain.Distance;
import subway.domain.Section;
import subway.domain.Station;

public class SectionCreateResponse {

    private final long id;
    private final Station upStation;
    private final Station downStation;
    private final Distance distance;

    public SectionCreateResponse(long id, Station upStation, Station downStation, Distance distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionCreateResponse of(Section section) {
        return new SectionCreateResponse(
                section.getId(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance()
        );
    }
}
