package portfolio.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class MockModel implements IPortfolios {

  StringBuilder log;
  boolean dataPresent;

  public MockModel(StringBuilder log, boolean dataPresent) {
    this.log = log;
    this.dataPresent = dataPresent;
  }

  @Override
  public String getPortfolioComposition(int portfolioId) {
    log.append(portfolioId).append(" ");
    if (!dataPresent) {
      throw new IllegalArgumentException();
    }
    if (portfolioId <= 0) {
      throw new IllegalArgumentException();
    }
    return "Composition ";
  }

  @Override
  public Double getPortfolioValue(LocalDate date, int portfolioId) {
    if(!dataPresent) {
      throw  new IllegalArgumentException();
    }
    log.append(date).append(" ").append(portfolioId);
    return 2d;
  }

  @Override
  public void savePortfolios() throws RuntimeException {
    if(!dataPresent) throw new RuntimeException("No portfolios to save\n");
  }

  @Override
  public void retrievePortfolios()
      throws IOException, ParserConfigurationException, SAXException {
    if(dataPresent) {
      throw new RuntimeException("Portfolios already populated\n");
    }
  }

  @Override
  public void createNewPortfolio(Map<String, Integer> stocks) {
    log.append(stocks).append(" ");
  }

  @Override
  public String getAvailablePortfolios() {
    if(!dataPresent) {
      throw new IllegalArgumentException("No Portfolios");
    }
    log.append("Available_Portfolios ");
    return "Available_Portfolios ";
  }

  @Override
  public boolean isTickerSymbolValid(String tickerSymbol) {
    log.append(tickerSymbol).append(" ");
    return "TICKER_SYMBOL".equals(tickerSymbol) || " TICKER_SYMBOL2".equals(tickerSymbol);
  }
}
