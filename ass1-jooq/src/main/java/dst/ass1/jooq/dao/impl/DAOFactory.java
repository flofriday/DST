package dst.ass1.jooq.dao.impl;

import dst.ass1.jooq.dao.*;
import org.jooq.DSLContext;

public class DAOFactory implements IDAOFactory {

  public DAOFactory(DSLContext dlsContext) {}

  @Override
  public IRiderPreferenceDAO createRiderPreferenceDao() {
    return null;
  }
}
