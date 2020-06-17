package com.sparrowwallet.sparrow.control;

import com.sparrowwallet.drongo.Utils;
import com.sparrowwallet.drongo.protocol.Sha256Hash;
import com.sparrowwallet.sparrow.AppController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.StyleClassValidationDecoration;

public class TransactionIdDialog extends Dialog<Sha256Hash> {
    private final CustomTextField txid;

    public TransactionIdDialog() {
        this.txid = (CustomTextField) TextFields.createClearableTextField();
        txid.setFont(Font.font ("Courier", txid.getFont().getSize()));
        final DialogPane dialogPane = getDialogPane();

        setTitle("Load Transaction");
        dialogPane.setHeaderText("Enter the transaction ID:");
        dialogPane.getStylesheets().add(AppController.class.getResource("general.css").toExternalForm());
        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL);
        dialogPane.setPrefWidth(550);
        dialogPane.setPrefHeight(200);

        Glyph wallet = new Glyph("FontAwesome", FontAwesome.Glyph.BITCOIN);
        wallet.setFontSize(50);
        dialogPane.setGraphic(wallet);

        final VBox content = new VBox(10);
        content.getChildren().add(txid);

        dialogPane.setContent(content);

        ValidationSupport validationSupport = new ValidationSupport();
        Platform.runLater(() -> {
            validationSupport.registerValidator(txid, Validator.combine(
                    Validator.createEmptyValidator("Transaction id is required"),
                    (Control c, String newValue) -> ValidationResult.fromErrorIf(c, "Transaction ID length incorrect", newValue.length() != 64),
                    (Control c, String newValue) -> ValidationResult.fromErrorIf(c, "Transaction ID must be hexadecimal", !Utils.isHex(newValue))
            ));
            validationSupport.setValidationDecorator(new StyleClassValidationDecoration());
        });

        final ButtonType okButtonType = new javafx.scene.control.ButtonType("Open Transaction", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(okButtonType);
        Button okButton = (Button) dialogPane.lookupButton(okButtonType);
        BooleanBinding isInvalid = Bindings.createBooleanBinding(() ->
                txid.getText().length() != 64 || !Utils.isHex(txid.getText()), txid.textProperty());
        okButton.disableProperty().bind(isInvalid);

        txid.setPromptText("f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16");
        txid.requestFocus();
        setResultConverter(dialogButton -> dialogButton == okButtonType ? Sha256Hash.wrap(txid.getText()) : null);
    }
}
