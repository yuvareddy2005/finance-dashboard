package com.reddy.finance_dashboard.service;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.stereotype.Service;

import com.reddy.finance_dashboard.entity.Transaction;
import com.reddy.finance_dashboard.service.rules.GroceriesRule;
import com.reddy.finance_dashboard.service.rules.TravelRule;

import jakarta.annotation.PostConstruct;

@Service
public class CategorizationService {

    private RulesEngine rulesEngine;
    private Rules rules;

    @PostConstruct
    public void init() {
        // Initialize the rules engine
        this.rulesEngine = new DefaultRulesEngine();

        // Register all our rule classes
        this.rules = new Rules();
        this.rules.register(new GroceriesRule());
        this.rules.register(new TravelRule());
        // We can add more rules here in the future
    }

    public void categorizeTransaction(Transaction transaction) {
        // Create a "Facts" object and add our transaction to it
        Facts facts = new Facts();
        facts.put("transaction", transaction);

        // Fire the rules engine!
        rulesEngine.fire(rules, facts);
    }
}
