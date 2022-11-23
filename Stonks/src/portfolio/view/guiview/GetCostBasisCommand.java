package portfolio.view.guiview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import portfolio.controller.Features;

class GetCostBasisCommand extends AbstractCommandHandlers implements CommandHandler{

  GetCostBasisCommand(JTextArea resultArea, Features features,
      JProgressBar progressBar, JFrame mainFrame) {
    super(resultArea, features, progressBar, mainFrame);
  }

  @Override
  public void execute() {
    JPanel availablePortfoliosDisplay;
    AtomicBoolean OKClicked = new AtomicBoolean(false);
    JDialog userInputDialog = getUserInputDialog("Get Portfolio Cost Basis");
    userInputDialog.setMinimumSize(new Dimension(470, 200));
    userInputDialog.setLocationRelativeTo(null);
    userInputDialog.setResizable(false);

    try {
      availablePortfoliosDisplay = getAvailablePortfoliosDisplay(features);
      availablePortfoliosDisplay.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
    } catch (Exception e) {
      resultArea.setText(e.getLocalizedMessage());
      progressBar.setIndeterminate(false);
      return;
    }

    JPanel datePortfolioIdPanel = new JPanel(new GridLayout(0, 2));
    JLabel dateLabel = new JLabel("Enter date (YYYY-MM-DD): ");
    JTextField dateValue = new JTextField();
    JLabel portfolioIdLabel = new JLabel("Enter portfolioId (Portfolio1 -> 1): ");
    JTextField portfolioIdValue = new JTextField();
    JButton OKButton = new JButton("OK");
    OKButton.addActionListener(e -> {
      userInputDialog.dispose();
      OKClicked.set(true);
    });

    datePortfolioIdPanel.add(dateLabel);
    datePortfolioIdPanel.add(dateValue);
    datePortfolioIdPanel.add(portfolioIdLabel);
    datePortfolioIdPanel.add(portfolioIdValue);

    JPanel fieldsPanel = new JPanel(new BorderLayout());
    fieldsPanel.add(datePortfolioIdPanel, BorderLayout.CENTER);
    fieldsPanel.add(OKButton, BorderLayout.EAST);
    fieldsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 2, 10));

    JLabel availablePortfolios = new JLabel("Available Portfolios");
    availablePortfolios.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

    userInputDialog.add(availablePortfolios, BorderLayout.PAGE_START);
    userInputDialog.add(availablePortfoliosDisplay, BorderLayout.CENTER);
    userInputDialog.add(fieldsPanel, BorderLayout.PAGE_END);

    userInputDialog.setVisible(true);

    if (OKClicked.get()) {
      GetCostBasisTask task = new GetCostBasisTask(features, dateValue.getText(),
          portfolioIdValue.getText());
      progressBar.setIndeterminate(true);
      task.execute();
      mainFrame.setEnabled(false);
    }
  }

  class GetCostBasisTask extends SwingWorker<String, Object> {
    Features features;
    String date;
    String portfolioId;

    GetCostBasisTask(Features features, String date, String portfolioId) {
      this.features = features;
      this.date = date;
      this.portfolioId = portfolioId;
    }

    @Override
    protected String doInBackground() throws Exception {
      try {
        return String.format("%.2f", features.getCostBasis(date, portfolioId));
      } catch (Exception e) {
        return e.getLocalizedMessage();
      }
    }

    @Override
    protected void done() {
      try {
        resultArea.setText("Cost basis on " + date + ": $" + get());
        mainFrame.setEnabled(true);
        progressBar.setIndeterminate(false);
      } catch (Exception ignore) {
      }
    }
  }
}
