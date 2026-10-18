
package com.codic.expensetracker;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ExpenseController {

    private final ExpenseRepository expenseRepository;

    public ExpenseController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // Display all expenses and calculate the total
    @GetMapping("/")
    public String showExpenses(Model model) {
        List<Expense> expenses = expenseRepository.findAll();

        double total = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        model.addAttribute("expenses", expenses);
        model.addAttribute("total", total);
        model.addAttribute("expense", new Expense());

        return "expenses";
    }

    // Add a new expense
    @PostMapping("/add")
    public String addExpense(
            @ModelAttribute("expense") Expense expense,
            RedirectAttributes redirectAttributes) {

        expense.setId(null);
        expenseRepository.save(expense);

        redirectAttributes.addFlashAttribute(
                "message", "Expense added successfully!");

        return "redirect:/";
    }

    // Open the form with the selected expense details
    @GetMapping("/edit/{id}")
    public String editExpense(
            @PathVariable Long id,
            Model model) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid expense ID"));

        List<Expense> expenses = expenseRepository.findAll();

        double total = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        model.addAttribute("expense", expense);
        model.addAttribute("expenses", expenses);
        model.addAttribute("total", total);

        return "expenses";
    }

    // Update the existing expense instead of creating a duplicate
    @PostMapping("/update/{id}")
    public String updateExpense(
            @PathVariable Long id,
            @ModelAttribute("expense") Expense updatedExpense,
            RedirectAttributes redirectAttributes) {

        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid expense ID"));

        existingExpense.setTitle(updatedExpense.getTitle());
        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setCategory(updatedExpense.getCategory());
        existingExpense.setDate(updatedExpense.getDate());

        expenseRepository.save(existingExpense);

        redirectAttributes.addFlashAttribute(
                "message", "Expense updated successfully!");

        return "redirect:/";
    }

    // Delete an expense
    @GetMapping("/delete/{id}")
    public String deleteExpense(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        expenseRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "message", "Expense deleted successfully!");

        return "redirect:/";
    }
}