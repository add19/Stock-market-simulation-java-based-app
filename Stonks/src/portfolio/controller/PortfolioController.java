package portfolio.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import portfolio.model.IPortfoliosModel;
import portfolio.view.IView;

/**
 * This class represents the implementation of the controller of this application. It takes user
 * inputs and performs the operations by forwarding the portfolio creation, save, retrieve,
 * composition and value to the model. Also takes model's response and outputs to the view.
 */
public class PortfolioController implements IPortfolioController {

  //region Variables
  protected static final String CREATE_PORTFOLIO_SUB_MENU =
      "Choose from the below menu: \n 1 -> Add a new stock "
          + "\n E -> Exit from the operation \n";
  protected static final String SAVE_RETRIEVE_PORTFOLIO_MENU =
      "Choose from the below menu: \n 1 -> Save portfolio "
          + "\n 2 -> Retrieve portfolio \n E -> Exit from the operation \n";
  protected static final String CHOOSE_FROM_AVAILABLE_PORTFOLIOS = "Choose from available portfolios "
      + "(eg: Portfolio1 -> give 1):\n";

  private final Readable in;
  private final IView view;
  //endregion

  /**
   * Constructs an object of Portfolio Controller and initializes its members accordingly.
   *
   * @param in   A {@link Readable} object to get user input.
   * @param view A {@link IView} object to display application operations/results.
   */
  public PortfolioController(Readable in, IView view) {
    this.in = in;
    this.view = view;
  }

  //region Public methods
  @Override
  public void run(IPortfoliosModel portfolios) throws IOException {
    Objects.requireNonNull(portfolios);
    Scanner scan = new Scanner(this.in);

    try {
      while (true) {
        view.displayMenu();
        view.askForInput();
        switch (scan.next()) {
          case "1":
            generatePortfolios(scan, portfolios);
            break;
          case "2":
            getPortfolioComposition(portfolios, scan);
            break;
          case "3":
            getPortfolioValuesForGivenDate(portfolios, scan);
            break;
          case "4":
            saveRetrievePortfolios(portfolios, scan);
            break;
          case "E":
            return;
          default:
            view.displayInvalidInput();
            scan.nextLine();
            break;
        }
      }
    } catch (NoSuchElementException e) {
      view.displayCustomText("\n----Exiting----\n");
    }

  }
  //endregion

  //region Private Methods
  protected void getPortfolioComposition(IPortfoliosModel portfolios, Scanner scan)
      throws IOException {
    String result;
    while (true) {
      try {
        view.displayCustomText(CHOOSE_FROM_AVAILABLE_PORTFOLIOS);
        String availablePortfolios = portfolios.getAvailablePortfolios();
        view.displayCustomText(availablePortfolios);
        view.askForInput();
        scan.nextLine();
        int portfolioId = scan.nextInt();
        result = portfolios.getPortfolioComposition(portfolioId);
        break;
      } catch (InputMismatchException | IllegalArgumentException e) {
        if ("No portfolios".equals(e.getMessage())) {
          view.displayCustomText("No portfolios\n");
          displayExitOperationSequence(scan);
          return;
        }
        view.displayInvalidInput();
      }
    }

    view.displayCustomText(result);
    displayExitOperationSequence(scan);
  }

  protected void saveRetrievePortfolios(IPortfoliosModel portfolios, Scanner scan)
      throws IOException {
    try {
      while (true) {
        view.displayCustomText(SAVE_RETRIEVE_PORTFOLIO_MENU);
        view.askForInput();
        switch (scan.next()) {
          case "E":
            return;
          case "1":
            portfolios.savePortfolios();
            view.displayCustomText("Saved\n");
            displayExitOperationSequence(scan);
            return;
          case "2":
            portfolios.retrievePortfolios();
            view.displayCustomText("Retrieved\n");
            displayExitOperationSequence(scan);
            return;
          default:
            view.displayInvalidInput();
            scan.nextLine();
            break;
        }
      }
    } catch (RuntimeException | ParserConfigurationException | SAXException e) {
      if ("No portfolios to save\n".equals(e.getMessage())
          || "Portfolios already populated\n".equals(e.getMessage())) {
        view.displayCustomText(e.getMessage());
      } else {
        view.displayCustomText("Encountered a problem while saving or retrieving \n");
      }
    } catch (FileNotFoundException e) {
      view.displayCustomText("No files found to retrieve\n");
    }
    displayExitOperationSequence(scan);
  }

  protected void getPortfolioValuesForGivenDate(IPortfoliosModel portfolios, Scanner scan)
      throws IOException {
    LocalDate date;
    int portfolioId;

    while (true) {
      try {
        view.displayCustomText(CHOOSE_FROM_AVAILABLE_PORTFOLIOS);
        String availablePortfolios = portfolios.getAvailablePortfolios();
        view.displayCustomText(availablePortfolios);
        view.askForInput();
        scan.nextLine();
        portfolioId = scan.nextInt();
        if (portfolioId <= 0) {
          throw new IllegalArgumentException();
        }
        view.displayCustomText("Please enter the date (yyyy-mm-dd): ");
        date = LocalDate.parse(scan.next());
        String portfolioValue = String.format("%.2f",
            portfolios.getPortfolioValue(date, portfolioId));
        view.displayCustomText("Portfolio" + portfolioId + "\n" + portfolioValue + "\n");
        break;
      } catch (DateTimeParseException | IllegalArgumentException | InputMismatchException e) {
        if ("No portfolios".equals(e.getMessage())) {
          view.displayCustomText("No portfolios\n");
          displayExitOperationSequence(scan);
          return;
        }
        view.displayInvalidInput();
      }
      catch (RuntimeException e) {
        view.displayCustomText("\nFailed to get value, please try again after some time...\n");
      }
    }

    displayExitOperationSequence(scan);
  }

  protected void generatePortfolios(Scanner scan, IPortfoliosModel portfolios)
      throws IOException {
    Map<String, Long> stocks = new HashMap<>();
    while (true) {
      view.displayCustomText(CREATE_PORTFOLIO_SUB_MENU);
      view.askForInput();
      switch (scan.next()) {
        case "E":
          if (stocks.size() > 0) {
            portfolios.createNewPortfolio(stocks);
          }
          return;
        case "1":
          addNewStock(scan, portfolios, stocks);
          break;
        default:
          view.displayInvalidInput();
          scan.nextLine();
          break;
      }
    }
  }

  protected void addNewStock(Scanner scan, IPortfoliosModel portfolios, Map<String, Long> stocks)
      throws IOException {
    String tickerSymbol;
    long stockQuantity;
    view.displayCustomText("Stock Symbol: ");
    scan.nextLine();
    tickerSymbol = scan.nextLine();
    while (!portfolios.isTickerSymbolValid(tickerSymbol)) {
      view.displayCustomText("Invalid Ticker Symbol\n");
      view.displayCustomText("Stock Symbol: ");
      tickerSymbol = scan.nextLine();
    }
    while (true) {
      view.displayCustomText("Stock Quantity: ");
      try {
        stockQuantity = scan.nextLong();
        if (stockQuantity <= 0) {
          throw new IllegalArgumentException();
        }
        break;
      } catch (InputMismatchException | IllegalArgumentException e) {
        scan.nextLine();
        view.displayInvalidInput();
      }
    }
    if (stocks.containsKey(tickerSymbol)) {
      stocks.put(tickerSymbol, stocks.get(tickerSymbol) + stockQuantity);
    }
    else {
      stocks.put(tickerSymbol, stockQuantity);
    }
  }

  protected void displayExitOperationSequence(Scanner scan) throws IOException {
    view.displayEscapeFromOperation();
    scan.nextLine();
    while (!"E".equals(scan.next())) {
      scan.nextLine();
      view.displayInvalidInput();
      view.displayEscapeFromOperation();
    }
  }
  //endregion
}
