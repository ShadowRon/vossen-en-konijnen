/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vossen_en_konijnen.view;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author R
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({vossen_en_konijnen.view.FieldViewTest.class, vossen_en_konijnen.view.AbstractViewTest.class, vossen_en_konijnen.view.BarViewTest.class, vossen_en_konijnen.view.LineViewTest.class, vossen_en_konijnen.view.PieViewTest.class})
public class ViewSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
