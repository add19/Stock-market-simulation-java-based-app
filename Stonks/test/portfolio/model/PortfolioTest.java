package portfolio.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Test;
import org.xml.sax.SAXException;

public class PortfolioTest {

  @Test
  public void testSetPortfolioStocks() {
    IStockService mockStockService = new MockStockService("/test/testData.txt");
    IPortfolio portfolio = new Portfolio(mockStockService);

    Map<IStock, Integer> map = new HashMap<>();
    map.put(new Stock("GOOG", mockStockService), 3);
    map.put(new Stock("PUBM", mockStockService), 1);
    map.put(new Stock("MSFT", mockStockService), 2);

    portfolio.setPortfolioStocks(map);

    String result = portfolio.getPortfolioComposition();
    assertTrue(result.contains("GOOG -> 3\n"));
    assertTrue(result.contains("MSFT -> 2\n"));
    assertTrue(result.contains("PUBM -> 1\n"));
  }

  @Test
  public void testSetPortfolioZeroStocks() {
    IStockService mockStockService = new MockStockService("/test/testData.txt");
    IPortfolio portfolio = new Portfolio(mockStockService);

    Map<IStock, Integer> map = new HashMap<>();

    portfolio.setPortfolioStocks(map);

    String result = portfolio.getPortfolioComposition();
    assertEquals("No stocks in the portfolio", result);
  }

  @Test
  public void getPortfolioComposition() {
    IStockService mockStockService = new MockStockService("/test/testData.txt");
    IPortfolio portfolio = new Portfolio(mockStockService);

    Map<IStock, Integer> map = new HashMap<>();
    map.put(new Stock("GOOG", mockStockService), 3);
    map.put(new Stock("PUBM", mockStockService), 1);
    map.put(new Stock("MSFT", mockStockService), 2);

    portfolio.setPortfolioStocks(map);

    String result = portfolio.getPortfolioComposition();
    assertTrue(result.contains("GOOG -> 3\n"));
    assertTrue(result.contains("MSFT -> 2\n"));
    assertTrue(result.contains("PUBM -> 1\n"));

    map.put(new Stock("ABC", mockStockService), 3);
    map.put(new Stock("DEF", mockStockService), 1);
    map.put(new Stock("GHI", mockStockService), 2);

    portfolio.setPortfolioStocks(map);

    result = portfolio.getPortfolioComposition();
    assertTrue(result.contains("ABC -> 3\n"));
    assertTrue(result.contains("GHI -> 2\n"));
    assertTrue(result.contains("DEF -> 1\n"));
    assertTrue(result.contains("GOOG -> 3\n"));
    assertTrue(result.contains("MSFT -> 2\n"));
    assertTrue(result.contains("PUBM -> 1\n"));

  }

  @Test
  public void testGetPortfolioValue() {
    IStockService mockStockService = new MockStockService("/test/testData.txt");
    IPortfolio portfolio = new Portfolio(mockStockService);

    Map<IStock, Integer> map = new HashMap<>();
    map.put(new Stock("GOOG", mockStockService), 3);
    map.put(new Stock("PUBM", mockStockService), 1);
    map.put(new Stock("MSFT", mockStockService), 2);

    portfolio.setPortfolioStocks(map);

    assertEquals(568.92, portfolio.getPortfolioValue(LocalDate.of(2022, 10, 28)), 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetPortfolioValueInvalidDate() {
    IStockService mockStockService = new MockStockService("/test/testData.txt");
    IPortfolio portfolio = new Portfolio(mockStockService);

    Map<IStock, Integer> map = new HashMap<>();
    map.put(new Stock("GOOG", mockStockService), 3);
    map.put(new Stock("PUBM", mockStockService), 1);
    map.put(new Stock("MSFT", mockStockService), 2);

    portfolio.setPortfolioStocks(map);

    assertEquals(568.92, portfolio.getPortfolioValue(LocalDate.of(2012, 10, 28)), 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetPortfolioValueInvalidFutureDate() {
    IStockService mockStockService = new MockStockService("/test/testData.txt");
    IPortfolio portfolio = new Portfolio(mockStockService);

    Map<IStock, Integer> map = new HashMap<>();
    map.put(new Stock("GOOG", mockStockService), 3);
    map.put(new Stock("PUBM", mockStockService), 1);
    map.put(new Stock("MSFT", mockStockService), 2);

    portfolio.setPortfolioStocks(map);

    assertEquals(568.92, portfolio.getPortfolioValue(LocalDate.of(2023, 10, 28)), 0.0);
  }

  @Test
  public void testGetEmptyPortfolioValue() {
    IStockService mockStockService = new MockStockService("/test/testData.txt");
    IPortfolio portfolio = new Portfolio(mockStockService);

    Map<IStock, Integer> map = new HashMap<>();

    portfolio.setPortfolioStocks(map);

    assertEquals(0, portfolio.getPortfolioValue(LocalDate.of(2022, 10, 28)), 0.0);
  }

  @Test
  public void testSavePortfolio() throws ParserConfigurationException {
    IStockService mockStockService = new MockStockService("/test/testData.txt");
    IPortfolio portfolio = new Portfolio(mockStockService);

    Map<IStock, Integer> map = new HashMap<>();
    map.put(new Stock("GOOG", mockStockService), 3);
    map.put(new Stock("PUBM", mockStockService), 1);
    map.put(new Stock("MSFT", mockStockService), 2);

    portfolio.setPortfolioStocks(map);
    String path = System.getProperty("user.dir") + "/test_save.xml";
    portfolio.savePortfolio(path);

    IPortfolio retrievedPortfolio = new Portfolio(mockStockService);

    try {
      retrievedPortfolio.retrievePortfolio(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }

    String result = retrievedPortfolio.getPortfolioComposition();
    assertTrue(result.contains("GOOG -> 3\n"));
    assertTrue(result.contains("MSFT -> 2\n"));
    assertTrue(result.contains("PUBM -> 1\n"));
    assertEquals(568.92, portfolio.getPortfolioValue(LocalDate.of(2022, 10, 28)), 0.0);

    try {
      Files.delete(Path.of(path));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test(expected = RuntimeException.class)
  public void testSaveEmptyPortfolio() throws IOException, ParserConfigurationException {
    IStockService mockStockService = new MockStockService("/test/testData.txt");
    IPortfolio portfolio = new Portfolio(mockStockService);

    Map<IStock, Integer> map = new HashMap<>();

    portfolio.setPortfolioStocks(map);
    String path = System.getProperty("user.dir") + "/test_save.xml";
    portfolio.savePortfolio(path);

    try {
      Files.delete(Path.of(path));
    } catch (IOException e) {
      throw e;
    }
  }

  @Test
  public void testSavePortfolioMultipleTimes() throws IOException, ParserConfigurationException {
    IStockService mockStockService = new MockStockService("/test/testData.txt");
    IPortfolio portfolio = new Portfolio(mockStockService);

    Map<IStock, Integer> map = new HashMap<>();
    map.put(new Stock("GOOG", mockStockService), 3);
    map.put(new Stock("PUBM", mockStockService), 1);
    map.put(new Stock("MSFT", mockStockService), 2);

    portfolio.setPortfolioStocks(map);
    String path = System.getProperty("user.dir") + "/test_multiple_save.xml";
    portfolio.savePortfolio(path);
    portfolio.savePortfolio(path);
    portfolio.savePortfolio(path);
    portfolio.savePortfolio(path);


    IPortfolio retrievedPortfolio = new Portfolio(mockStockService);

    try {
      retrievedPortfolio.retrievePortfolio(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }

    String result = retrievedPortfolio.getPortfolioComposition();
    assertTrue(result.contains("GOOG -> 3\n"));
    assertTrue(result.contains("MSFT -> 2\n"));
    assertTrue(result.contains("PUBM -> 1\n"));
    assertEquals(568.92, retrievedPortfolio.getPortfolioValue(LocalDate.of(2022, 10, 28)), 0.0);

    try {
      Files.delete(Path.of(path));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test(expected = IOException.class)
  public void retrievePortfolioInvalidPath()
    throws IOException, ParserConfigurationException, SAXException {
    IStockService mockStockService = new MockStockService("/test/testData.txt");
    String path = System.getProperty("user.dir") + "/invalid.xml";

    IPortfolio retrievedPortfolio = new Portfolio(mockStockService);

    try {
      retrievedPortfolio.retrievePortfolio(path);
    } catch (Exception e) {
      throw e;
    }

    String result = retrievedPortfolio.getPortfolioComposition();
    assertTrue(result.contains("GOOG -> 3\n"));
    assertTrue(result.contains("MSFT -> 2\n"));
    assertTrue(result.contains("PUBM -> 1\n"));
    assertEquals(568.92, retrievedPortfolio.getPortfolioValue(LocalDate.of(2022, 10, 28)), 0.0);
  }

  @Test(expected = RuntimeException.class)
  public void testRetrievePortfolioMultipleTimes() throws IOException, ParserConfigurationException {
    IStockService mockStockService = new MockStockService("/test/testData.txt");
    IPortfolio portfolio = new Portfolio(mockStockService);

    Map<IStock, Integer> map = new HashMap<>();
    map.put(new Stock("GOOG", mockStockService), 3);
    map.put(new Stock("PUBM", mockStockService), 1);
    map.put(new Stock("MSFT", mockStockService), 2);

    portfolio.setPortfolioStocks(map);
    String path = System.getProperty("user.dir") + "/test_multiple_save.xml";
    portfolio.savePortfolio(path);
    portfolio.savePortfolio(path);
    portfolio.savePortfolio(path);
    portfolio.savePortfolio(path);


    IPortfolio retrievedPortfolio = new Portfolio(mockStockService);

    try {
      retrievedPortfolio.retrievePortfolio(path);
      retrievedPortfolio.retrievePortfolio(path);
    } catch (IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }

    String result = retrievedPortfolio.getPortfolioComposition();
    assertTrue(result.contains("GOOG -> 3\n"));
    assertTrue(result.contains("MSFT -> 2\n"));
    assertTrue(result.contains("PUBM -> 1\n"));
    assertEquals(568.92, retrievedPortfolio.getPortfolioValue(LocalDate.of(2022, 10, 28)), 0.0);

    try {
      Files.delete(Path.of(path));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}