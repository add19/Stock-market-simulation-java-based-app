package portfolio.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class FlexiblePortfoliosModelImplTest {

  private IStockService mockExtensive;
  private AbstractPortfolioModel portfolios;
  private AbstractPortfolioModel portfolioMockModel = new MockModelForFlexiPortfolio();

  @Before
  public void setup() throws IllegalAccessException, NoSuchFieldException {
    mockExtensive = new MockStockService("/test/testExtensiveData.txt");
    portfolios = new FlexiblePortfoliosModelImpl();
    portfolioMockModel = new MockModelForFlexiPortfolio();

    Field stockService = AbstractPortfolioModel.class.getDeclaredField("stockService");
    stockService.setAccessible(true);
    stockService.set(portfolios, mockExtensive);

    stockService = AbstractPortfolioModel.class.getDeclaredField("stockService");
    stockService.setAccessible(true);
    stockService.set(portfolioMockModel, mockExtensive);
  }

  @Test
  public void testCreatePortfolio() {

    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.createNewPortfolio(map);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    String result = portfolios.getPortfolioComposition(1);
    assertTrue(result.contains("GOOG -> 3\n"));
    assertTrue(result.contains("PUBM -> 2\n"));
    assertTrue(result.contains("MSFT -> 1\n"));
    assertTrue(result.contains("MUN -> 12\n"));


    map = new HashMap<>();
    map.put("AAPL", 7L);
    map.put("OCL", 9L);

    portfolios.createNewPortfolio(map);

    result = portfolios.getPortfolioComposition(2);

    assertTrue(result.contains("OCL -> 9\n"));
    assertTrue(result.contains("AAPL -> 7\n"));
  }

  @Test
  public void testAddStocksToPortfolio() {

    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.createNewPortfolio(map);

    portfolios.addStocksToPortfolio("GOOG", 1L, 1, LocalDate.now());
    portfolios.getPortfolioComposition(1);
    String result = portfolios.getPortfolioComposition(1);
    assertTrue(result.contains("GOOG -> 4\n"));
    assertTrue(result.contains("PUBM -> 2\n"));
    assertTrue(result.contains("MSFT -> 1\n"));
    assertTrue(result.contains("MUN -> 12\n"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddStocksToPortfolioInvalidDate() {

    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.createNewPortfolio(map);

    portfolios.addStocksToPortfolio("GOOG", 1L, 1, LocalDate.of(2022,10,10));
    portfolios.getPortfolioComposition(1);
    String result = portfolios.getPortfolioComposition(1);
    assertTrue(result.contains("GOOG -> 4\n"));
    assertTrue(result.contains("PUBM -> 2\n"));
    assertTrue(result.contains("MSFT -> 1\n"));
    assertTrue(result.contains("MUN -> 12\n"));
  }

  @Test
  public void testSellStockFromPortfolio() {

    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.createNewPortfolio(map);

    portfolios.sellStockFromPortfolio("GOOG", 1L, 1, LocalDate.now());
    portfolios.getPortfolioComposition(1);
    String result = portfolios.getPortfolioComposition(1);
    assertTrue(result.contains("GOOG -> 2\n"));
    assertTrue(result.contains("PUBM -> 2\n"));
    assertTrue(result.contains("MSFT -> 1\n"));
    assertTrue(result.contains("MUN -> 12\n"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSellStockFromPortfolioInvalidDate() {

    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.createNewPortfolio(map);

    portfolios.sellStockFromPortfolio("GOOG", 1L, 1, LocalDate.of(2022,10,10));
    portfolios.getPortfolioComposition(1);
    String result = portfolios.getPortfolioComposition(1);
    assertTrue(result.contains("GOOG -> 2\n"));
    assertTrue(result.contains("PUBM -> 2\n"));
    assertTrue(result.contains("MSFT -> 1\n"));
    assertTrue(result.contains("MUN -> 12\n"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddStocksToPortfolioInvalidPortfolio() {

    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.createNewPortfolio(map);

    portfolios.addStocksToPortfolio("GOOG", 1L, 2, LocalDate.now());
    portfolios.getPortfolioComposition(1);
    String result = portfolios.getPortfolioComposition(2);
    assertTrue(result.contains("GOOG -> 4\n"));
    assertTrue(result.contains("PUBM -> 2\n"));
    assertTrue(result.contains("MSFT -> 1\n"));
    assertTrue(result.contains("MUN -> 12\n"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSellStocksFromPortfolioInvalidPortfolio() {

    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.createNewPortfolio(map);

    portfolios.sellStockFromPortfolio("GOOG", 1L, 2, LocalDate.now());
    portfolios.getPortfolioComposition(1);
    String result = portfolios.getPortfolioComposition(2);
    assertTrue(result.contains("GOOG -> 4\n"));
    assertTrue(result.contains("PUBM -> 2\n"));
    assertTrue(result.contains("MSFT -> 1\n"));
    assertTrue(result.contains("MUN -> 12\n"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSellStocksFromPortfolioInvalidQty() {

    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.createNewPortfolio(map);

    portfolios.sellStockFromPortfolio("GOOG", -1L, 1, LocalDate.now());
    portfolios.getPortfolioComposition(1);
    String result = portfolios.getPortfolioComposition(1);
    assertTrue(result.contains("GOOG -> 4\n"));
    assertTrue(result.contains("PUBM -> 2\n"));
    assertTrue(result.contains("MSFT -> 1\n"));
    assertTrue(result.contains("MUN -> 12\n"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddStocksToPortfolioInvalidQty() {

    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.createNewPortfolio(map);

    portfolios.addStocksToPortfolio("GOOG", -1L, 1, LocalDate.now());
    portfolios.getPortfolioComposition(1);
    String result = portfolios.getPortfolioComposition(1);
    assertTrue(result.contains("GOOG -> 4\n"));
    assertTrue(result.contains("PUBM -> 2\n"));
    assertTrue(result.contains("MSFT -> 1\n"));
    assertTrue(result.contains("MUN -> 12\n"));
  }

  @Test
  public void testGetCostBasis() {
    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.setCommissionFee(10);
    portfolios.createNewPortfolio(map);

    assertEquals(1735.06, portfolios.getCostBasis(LocalDate.now(), 1), 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetCostBasisInvalidDate() {
    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.setCommissionFee(10);
    portfolios.createNewPortfolio(map);

    assertEquals(1735.06, portfolios.getCostBasis(LocalDate.of(2022,10,10), 1), 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetCostBasisInvalidPortfolio() {
    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.setCommissionFee(10);
    portfolios.createNewPortfolio(map);

    assertEquals(1735.06, portfolios.getCostBasis(LocalDate.now(), 2), 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetCostBasisNegativePortfolio() {
    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.setCommissionFee(10);
    portfolios.createNewPortfolio(map);

    assertEquals(1735.06, portfolios.getCostBasis(LocalDate.now(), -1), 0.0);
  }

  @Test
  public void testSetTransactionFeeAndGetCostBasis() {
    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);
    map.put("PUBM", 2L);
    map.put("MSFT", 1L);
    map.put("MUN", 12L);

    portfolios.setCommissionFee(10);
    portfolios.createNewPortfolio(map);

    portfolios.addStocksToPortfolio("GOOG", 1L, 1, LocalDate.now());
    portfolios.getPortfolioComposition(1);
    assertEquals(1789.23, portfolios.getPortfolioValue(LocalDate.now(), 1), 0.0);
    assertEquals(1839.23, portfolios.getCostBasis(LocalDate.now(), 1), 0.0);

    String result = portfolios.getPortfolioComposition(1);
    assertTrue(result.contains("GOOG -> 4\n"));
    assertTrue(result.contains("PUBM -> 2\n"));
    assertTrue(result.contains("MSFT -> 1\n"));
    assertTrue(result.contains("MUN -> 12\n"));
  }

  @Test
  public void testGetPortfolioPerformance()
    throws IOException, ParserConfigurationException, SAXException {

    portfolioMockModel.retrievePortfolios();

    LocalDate startDate = LocalDate.of(2019,10,24);
    LocalDate endDate = LocalDate.of(2019,11,30);

    String expectedString = "Performance of portfolio XXX from 2019-10-24 to 2019-11-30\n"
      + "2019-10-31: \n"
      + "2019-11-07: *****\n"
      + "2019-11-14: ******\n"
      + "2019-11-21: *************************************************\n"
      + "2019-11-28: **************************************************\n"
      + "Scale: * = $59\n";

    assertEquals(expectedString, portfolioMockModel.getPortfolioPerformance(1, startDate, endDate));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetPortfolioPerformanceInvalidId()
    throws IOException, ParserConfigurationException, SAXException {

    portfolioMockModel.retrievePortfolios();

    LocalDate startDate = LocalDate.of(2019,10,24);
    LocalDate endDate = LocalDate.of(2019,11,30);

    String expectedString = "Performance of portfolio XXX from 2019-10-24 to 2019-11-30\n"
      + "2019-10-31: \n"
      + "2019-11-07: *****\n"
      + "2019-11-14: ******\n"
      + "2019-11-21: *************************************************\n"
      + "2019-11-28: **************************************************\n"
      + "Scale: * = $59\n";

    assertEquals(expectedString, portfolioMockModel.getPortfolioPerformance(2, startDate, endDate));
  }

  @Test
  public void testSetCommissionFee() {
    Map<String, Long> map = new HashMap<>();
    map.put("GOOG", 3L);

    portfolios.setCommissionFee(10);
    portfolios.createNewPortfolio(map);

    assertEquals(292.51, portfolios.getCostBasis(LocalDate.now(), 1), 0.0);
    portfolios.sellStockFromPortfolio("GOOG", 1L, 1, LocalDate.now());
    assertEquals(302.51, portfolios.getCostBasis(LocalDate.now(), 1), 0.0);
  }

  @Test
  public void testGetPortfolioCompositionOnAGivenDate()
    throws IOException, ParserConfigurationException, SAXException {

    portfolioMockModel.retrievePortfolios();
    portfolioMockModel.setCommissionFee(10);
    portfolioMockModel.getPortfolioComposition(1);

    String result = portfolioMockModel.getPortfolioCompositionOnADate(1,
        LocalDate.of(2019,11,11));

    assertTrue(result.contains("AAPL -> 2\n"));
    assertTrue(result.contains("GOOG -> 2\n"));
    assertTrue(result.contains("A -> 2\n"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetPortfolioCompositionOnInvalidGivenDate()
    throws IOException, ParserConfigurationException, SAXException {

    portfolioMockModel.retrievePortfolios();
    portfolioMockModel.setCommissionFee(10);
    portfolioMockModel.getPortfolioComposition(1);

    String result = portfolioMockModel.getPortfolioCompositionOnADate(1,
      LocalDate.of(2015,11,11));

    assertTrue(result.contains("AAPL -> 2\n"));
    assertTrue(result.contains("GOOG -> 2\n"));
    assertTrue(result.contains("A -> 2\n"));
  }

  @Test
  public void testGetAvailablePortfolios()
    throws IOException, ParserConfigurationException, SAXException {

    portfolioMockModel.retrievePortfolios();
    portfolioMockModel.setCommissionFee(10);
    portfolioMockModel.getPortfolioComposition(1);

    String result = portfolioMockModel.getAvailablePortfolios();
    assertTrue(result.equals("Portfolio1 -> test_any_date\n"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetAvailablePortfoliosNothing()
    throws IOException, ParserConfigurationException, SAXException {

    portfolioMockModel.setCommissionFee(10);
    portfolioMockModel.getPortfolioComposition(1);

    String result = portfolioMockModel.getAvailablePortfolios();
    assertTrue(result.equals("Portfolio1 -> test_any_date\n"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetPortfolioCompositionNegativeId()
    throws IOException, ParserConfigurationException, SAXException {
    portfolios.getPortfolioComposition(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetPortfolioCompositionInvalidId()
    throws IOException, ParserConfigurationException, SAXException {
    portfolios.getPortfolioComposition(2);
  }

  @Test(expected = RuntimeException.class)
  public void testMultipleRetrieve()
    throws IOException, ParserConfigurationException, SAXException {
    portfolioMockModel.retrievePortfolios();
    portfolioMockModel.retrievePortfolios();
  }
}