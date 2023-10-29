package datamaintain.samples;

import datamaintain.core.Datamaintain;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
class DatamaintainMigrationInitializer implements InitializingBean, Ordered {
    private final Datamaintain datamaintain;

    public DatamaintainMigrationInitializer(Datamaintain datamaintain) {
        this.datamaintain = datamaintain;
    }

    @Override
    public void afterPropertiesSet() {
        datamaintain.updateDatabase();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}