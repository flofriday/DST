package dst.ass1.jpa.tests;

import dst.ass1.jpa.ORMService;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.util.Constants;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests the constraint for IRider bank data.
 */
public class Ass1_1_2_01Test {

    @Rule
    public ORMService orm = new ORMService();

    @Test
    public void testRiderAccountNoBankCodeConstraint() {
        IRider r1 = orm.getModelFactory().createRider();
        IRider r2 = orm.getModelFactory().createRider();

        r1.setEmail("p1@example.com");
        r2.setEmail("p2@example.com");
        r1.setTel("tel1");
        r2.setTel("tel2");

        new UniqueConstraintTester<>(r1, r2, e -> {
            e.setAccountNo("uniqueVal1");
            e.setBankCode("uniqueVal2");
        }).run(orm.getEntityManager());
    }

    @Test
    public void testRiderAccountNoBankCodeConstraintJdbc() {
        assertTrue(orm.getDatabaseGateway().isIndex(Constants.T_RIDER, Constants.M_RIDER_ACCOUNT, false));
        assertTrue(orm.getDatabaseGateway().isIndex(Constants.T_RIDER, Constants.M_RIDER_BANK_CODE, false));
        assertTrue(orm.getDatabaseGateway().isComposedIndex(Constants.T_RIDER, Constants.M_RIDER_ACCOUNT, Constants.M_RIDER_BANK_CODE));

        assertTrue(orm.getDatabaseGateway().isNullable(Constants.T_RIDER, Constants.M_RIDER_ACCOUNT));
        assertTrue(orm.getDatabaseGateway().isNullable(Constants.T_RIDER, Constants.M_RIDER_BANK_CODE));
    }

}
