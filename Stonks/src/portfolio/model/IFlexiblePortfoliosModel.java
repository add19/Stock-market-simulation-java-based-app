package portfolio.model;

import java.time.LocalDate;
import java.util.Map;

/**
 * This interface represents flexible portfolios allowing operations which weren't allowed in the
 * original portfolios model.
 */
public interface IFlexiblePortfoliosModel extends IPortfoliosModel {

  /**
   * Creates new portfolio on the specified date.
   *
   * @param stocks mapping of stock ticker symbols and quantities.
   * @param date the date on which the portfolio is to be created.
   */
  void createNewPortfolioOnADate(Map<String, Long> stocks, LocalDate date);

  /**
   * Adds new stocks and their respective quantities to an existing portfolio.
   *
   * @param tickerSymbol
   * @param quantity
   * @param portfolioId
   */
  void addStocksToPortfolio(String tickerSymbol, Long quantity, int portfolioId, LocalDate date);

  /**
   * Sells current stocks and their respective quantities from the given portfolio.
   *
   * @param tickerSymbol
   * @param quantity
   * @param portfolioId
   * @throws IllegalArgumentException
   */
  void sellStockFromPortfolio(String tickerSymbol, Long quantity, int portfolioId, LocalDate date)
      throws IllegalArgumentException;

  /**
   * Determines the cost basis of a specified portfolio.
   *
   * @param date
   * @param portfolioId
   * @return
   */
  double getCostBasis(LocalDate date, int portfolioId);

  String getPortfolioPerformance(int portfolioId, LocalDate rangeStart, LocalDate rangeEnd);

  String getPortfolioCompositionOnADate(int portfolioId, LocalDate date);

  void setServiceType(ServiceType serviceType);

  void setCommissionFee(double commissionFee);

}
