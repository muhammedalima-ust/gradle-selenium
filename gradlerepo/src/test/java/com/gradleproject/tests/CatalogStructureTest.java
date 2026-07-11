package com.gradleproject.tests;

import com.gradleproject.pages.CatalogPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static java.lang.reflect.Modifier.*;
import static org.junit.jupiter.api.Assertions.*;
public class CatalogStructureTest {

    @Test
    @DisplayName("Refracting Test case")
    void refractoringTheExistingProject(){
          List<Field> catalogLocators = Arrays.stream(CatalogPage.class.getDeclaredFields())
                  .filter(field -> field.getType().equals(By.class))
                  .toList();
          List<Integer> catalogModifiers = catalogLocators.stream()
                  .map(modifier -> modifier.getModifiers())
                  .toList();

          assertAll(
                  ()->assertTrue(catalogModifiers.stream()
                          .allMatch(Private-> isPrivate(Private))),

                  ()-> assertTrue(catalogModifiers.stream()
                          .allMatch(Static -> isStatic(Static))),

                  ()-> assertTrue(catalogModifiers.stream()
                          .allMatch(Final ->isFinal(Final)))

          );

         List<Method> catalogMethodModiers = Arrays.stream(CatalogPage.class.getMethods())
                 .filter(method -> method.getReturnType().equals(CatalogPage.class))
                 .toList();

        System.out.println();

        assertAll(
                ()->assertTrue(catalogMethodModiers.stream()
                        .allMatch(Public-> isPublic(Public.getModifiers()))),

                ()->assertTrue(catalogMethodModiers.stream()
                        .allMatch(returnType ->returnType.getReturnType().equals(CatalogPage.class)))
        );



    }


}