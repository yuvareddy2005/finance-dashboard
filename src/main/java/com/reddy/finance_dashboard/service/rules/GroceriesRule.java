package com.reddy.finance_dashboard.service.rules;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import com.reddy.finance_dashboard.entity.Transaction;

@Rule(name = "Groceries Rule", description = "Categorize transactions from grocery stores")
public class GroceriesRule {

    @Condition
    public boolean isGroceryStore(@Fact("transaction") Transaction transaction) {
        // Condition: Check if the transaction description contains keywords for groceries.
        String description = transaction.getDescription().toLowerCase();
        return description.contains("safeway") || description.contains("kroger") || description.contains("market");
    }

    @Action
    public void setCategoryToGroceries(@Fact("transaction") Transaction transaction) {
        // Action: If the condition is true, set the category to "Groceries".
        transaction.setCategory("Groceries");
    }
}
