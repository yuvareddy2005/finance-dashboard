package com.reddy.finance_dashboard.service.rules;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import com.reddy.finance_dashboard.entity.Transaction;

@Rule(name = "Travel Rule", description = "Categorize transactions from airlines and travel agencies")
public class TravelRule {

    @Condition
    public boolean isTravel(@Fact("transaction") Transaction transaction) {
        // Condition: Check if the transaction description contains keywords for travel.
        String description = transaction.getDescription().toLowerCase();
        return description.contains("airline") || description.contains("expedia") || description.contains("booking");
    }

    @Action
    public void setCategoryToTravel(@Fact("transaction") Transaction transaction) {
        // Action: If the condition is true, set the category to "Travel".
        transaction.setCategory("Travel");
    }
}
