package portfolio.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Abstract class to store common logic and abstract method declarations for a portfolio model.
 */
public abstract class AbstractPortfolioModel implements IFlexiblePortfoliosModel {

  Map<String, AbstractPortfolio> portfolioMap;
  IStockService stockService;
  IStockAPIOptimizer apiOptimizer;

  @Override
  public void setServiceType(ServiceType serviceType) {
    stockService = AbstractServiceCreator.serviceCreator(serviceType);
  }

  @Override
  public void createNewPortfolioOnADate(Map<String, Long> stocks, LocalDate date) {
    Map<IStock, Long> stockQty = new HashMap<>();

    for (Map.Entry<String, Long> entry : stocks.entrySet()) {
      IStock stock = new Stock(entry.getKey(), this.stockService);
      apiOptimizer.cacheSetObj(entry.getKey(), stock);
      stockQty.put(stock, entry.getValue());
    }

    AbstractPortfolio portfolio = createPortfolio(stockQty, date);
    portfolioMap.put(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
        portfolio);
  }

  @Override
  public void createNewPortfolio(Map<String, Long> stocks) {
    createNewPortfolioOnADate(stocks, LocalDate.now());
  }

  @Override
  public Double getPortfolioValue(LocalDate date, int portfolioId) throws IllegalArgumentException {
    if (date.isAfter(LocalDate.now()) || portfolioId > portfolioMap.size() || portfolioId <= 0) {
      throw new IllegalArgumentException();
    }

    return getPortfolioFromMap(portfolioId).getValue().getPortfolioValue(date);
  }

  @Override
  public String getAvailablePortfolios() throws RuntimeException {
    int portfolioNo = 0;
    StringBuilder composition = new StringBuilder();

    if (portfolioMap.size() > 0) {
      for (Entry<String, AbstractPortfolio> entry : portfolioMap.entrySet()) {
        composition.append("Portfolio").append(portfolioNo + 1).append("(Creation datetime: ")
            .append(entry.getKey()).append(")\n");
        portfolioNo++;
      }
    } else {
      throw new IllegalArgumentException("No portfolios");
    }
    return composition.toString();
  }

  @Override
  public boolean isTickerSymbolValid(String tickerSymbol) {
    return this.stockService.getValidStockSymbols().contains(tickerSymbol);
  }

  @Override
  public String getPortfolioComposition(int portfolioId) {
    if (portfolioId > portfolioMap.size() || portfolioId <= 0) {
      throw new IllegalArgumentException();
    }

    StringBuilder composition = new StringBuilder();
    composition.append("Portfolio").append(portfolioId).append("\n")
        .append(getPortfolioFromMap(portfolioId).getValue().getPortfolioComposition()).append("\n");
    return composition.toString();
  }

  @Override
  public void savePortfolios() throws RuntimeException, ParserConfigurationException {
    if (portfolioMap.size() == 0) {
      throw new RuntimeException("No portfolios to save\n");
    }

    String userDirectory = System.getProperty("user.dir");
    for (Entry<String, AbstractPortfolio> entry : portfolioMap.entrySet()) {
      entry.getValue().savePortfolio(userDirectory + "/" + getPath() + entry.getKey() + ".xml");
    }
  }

  @Override
  public void retrievePortfolios()
      throws IOException, ParserConfigurationException, SAXException {
    if (portfolioMap.size() > 0) {
      throw new RuntimeException("Portfolios already populated\n");
    }

    String userDirectory = System.getProperty("user.dir") + "/" + getPath();
    File dir = new File(userDirectory);
    File[] files = dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".xml"));

    if (files != null && files.length != 0) {
      for (File file : files) {
        AbstractPortfolio portfolio = createPortfolio(new HashMap<>(), LocalDate.now());
        try {
          portfolio.retrievePortfolio(userDirectory + file.getName());
        } catch (IllegalArgumentException e) {
          // TODO add some handling here.
        }
        portfolioMap.put(file.getName().replaceAll(".xml", ""), portfolio);
      }
    } else {
      throw new FileNotFoundException("No portfolios found to retrieve");
    }
  }

  protected Entry<String, AbstractPortfolio> getPortfolioFromMap(int portfolioId) {
    int counter = 1;
    Iterator<Entry<String, AbstractPortfolio>> entryIterator = portfolioMap.entrySet().iterator();
    Entry<String, AbstractPortfolio> valueToCheck = entryIterator.next();

    while (counter != portfolioId && entryIterator.hasNext()) {
      valueToCheck = entryIterator.next();
      counter++;
    }
    return valueToCheck;
  }

  protected abstract AbstractPortfolio createPortfolio(Map<IStock, Long> stockQty, LocalDate date);

  protected abstract String getPath();

}
