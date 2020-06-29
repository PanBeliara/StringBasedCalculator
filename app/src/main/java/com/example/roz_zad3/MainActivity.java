package com.example.roz_zad3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    EditText result_view;

    boolean numberBuilding = false;
    boolean minusAdded = false;
    String equation = "";

    List<String> elements;
    List<String> newElements;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.result_view = findViewById(R.id.result_field);
    }
    private void refresh()
    {
        this.result_view.setText(this.equation);
    }
    public void clear(View view)
    {
        this.equation = "";
        refresh();
    }
    public void buttonClick(View view)
    {
        String tag = view.getTag().toString();

        if(tag.equals("="))
            conclude();
        else if(MyTools.isNumber(tag))
            addToNumber(tag);
        else
            addSymbol(tag);
    }
    private void addToNumber(String digit)
    {
        this.equation += digit;
        refresh();
        this.numberBuilding = true;
    }
    private void addSymbol(String symbol)
    {
        if(symbol.equals("-") && canInsertMinus())
        {
            this.equation += symbol;
            this.minusAdded = true;
            refresh();
        }
        else if(numberBuilding)
        {
            this.equation += " " + symbol + " ";
            this.numberBuilding = false;
            this.minusAdded = false;
            refresh();
        }
    }
    private boolean canInsertMinus()
    {
        return (!this.numberBuilding && !this.minusAdded);
    }

    private void conclude()
    {
        if(!this.numberBuilding) //panic, example equation state: "43 + 22 + "
            return;

        this.elements = Arrays.asList(this.equation.split(" "));
        this.newElements = new ArrayList<>();

        if(!conductOperations(Operations.MultiplicationDividing))
            return; //panic

        if(!conductOperations(Operations.AdditionSubtraction))
            return; //panic

        this.equation = "= " + this.elements.get(0);

        refresh();
        clearCache();
        this.equation = "";
        this.numberBuilding = false;
        this.minusAdded = false;
    }
    private boolean conductOperations(Operations operations)
    {
        switch(operations)
        {
            case AdditionSubtraction:
                try
                {
                    calculateAddingSubtraction();
                }
                catch(Exception e)
                {
                    displayErrorAndResetEquation();
                    return false;
                }
                break;
            case MultiplicationDividing:
                try
                {
                    calculateMultiplicationsDividing();
                }
                catch(Exception e)
                {
                    displayErrorAndResetEquation();
                    return false;
                }
                break;
        }

        return true;
    }
    private void calculateMultiplicationsDividing() throws UnsupportedOperationException
    {
        int i=0;
        double tempResult = Double.parseDouble(this.elements.get(0));

        while(i < (this.elements.size() - 1))
        {
            if(this.elements.get(i+1).equals("*"))
            {
                tempResult *= Double.parseDouble(this.elements.get(i+2));
            }
            else if(this.elements.get(i+1).equals("/"))
            {
                double denominator = Double.parseDouble(this.elements.get(i+2));
                if(denominator == 0)
                    throw new ArithmeticException();
                tempResult /= denominator;
            }
            else if(this.elements.get(i+1).equals("+")  || this.elements.get(i+1).equals("-"))
            {
                this.newElements.add(String.valueOf(tempResult));
                this.newElements.add(this.elements.get(i+1));
                tempResult = Double.parseDouble(this.elements.get(i+2));
            }
            else //panic
            {
                throw new UnsupportedOperationException();
            }
            i += 2;
        }
        this.newElements.add(String.valueOf(tempResult));
        this.elements = new ArrayList<>(this.newElements);
        this.newElements = new ArrayList<>();
    }

    private void calculateAddingSubtraction() throws UnsupportedOperationException
    {
        int i = 0;
        double tempResult = Double.parseDouble(this.elements.get(0));

        while(i < (this.elements.size() - 1))
        {
            if(this.elements.get(i+1).equals("+"))
            {
                tempResult += Double.parseDouble(this.elements.get(i+2));
            }
            else if(this.elements.get(i+1).equals("-"))
            {
                tempResult -= Double.parseDouble(this.elements.get(i+2));
            }
            else //panic
            {
                throw new UnsupportedOperationException();
            }
            i += 2;
        }
        this.newElements.add(String.valueOf(tempResult));
        this.elements = new ArrayList<>(this.newElements);
        this.newElements = new ArrayList<>();
    }

    private void displayErrorAndResetEquation()
    {
        EditText result_view = findViewById(R.id.result_field);
        result_view.setText("Error");
        this.equation = "";
        clearCache();
    }
    private void clearCache()
    {
        this.elements = null;
        this.newElements = null;
    }

    private enum Operations {
        AdditionSubtraction, MultiplicationDividing
    }
}


class MyTools
{
    static boolean isNumber(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
