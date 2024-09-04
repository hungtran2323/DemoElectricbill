import com.example.electricitybill.model.ElectricityRate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ElectricityRateService {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Transactional
    public ElectricityRate updateElectricityRateUsingQueryDSL(ElectricityRate electricityRate) {
        QElectricityRate qElectricityRate = QElectricityRate.electricityRate;

        queryFactory.update(qElectricityRate)
                .where(qElectricityRate.tier.eq(electricityRate.getTier()))
                .set(qElectricityRate.rate, electricityRate.getRate())
                .set(qElectricityRate.threshold, electricityRate.getThreshold())
                .execute();

        return electricityRate;
    }
}