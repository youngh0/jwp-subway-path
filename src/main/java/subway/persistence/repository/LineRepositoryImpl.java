package subway.persistence.repository;

import org.springframework.stereotype.Repository;
import subway.persistence.dao.LineDao;
import subway.persistence.dao.entity.LineEntity;
import subway.service.line.LineRepository;
import subway.service.line.domain.Line;

import java.util.Optional;

@Repository
public class LineRepositoryImpl implements LineRepository {

    private final LineDao lineDao;

    public LineRepositoryImpl(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Override
    public Line insert(Line line) {
        LineEntity lineEntity = new LineEntity(line.getName(), line.getColor());
        LineEntity savedLineEntity = lineDao.insert(lineEntity);
        return toDomain(savedLineEntity);
    }

    @Override
    public Line findById(long lineId) {
        Optional<LineEntity> foundLineEntity = lineDao.findLineById(lineId);
        if (foundLineEntity.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
        return toDomain(foundLineEntity.get());
    }

    private Line toDomain(LineEntity lineEntity) {
        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor());
    }
}
