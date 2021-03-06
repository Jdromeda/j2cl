/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.j2cl.generator;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Streams;
import com.google.j2cl.ast.AbstractVisitor;
import com.google.j2cl.ast.MemberDescriptor;
import com.google.j2cl.ast.Type;
import com.google.j2cl.ast.Variable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Traverses a Type and gathers variable names in each method and creates non colliding local
 * aliases for variables that collide with import module names or collide with JavaScript keywords.
 */
class VariableAliasesGatherer extends AbstractVisitor {

  /**
   * Returns the aliases of the variables whose names collide with import aliases or Javascript
   * keywords.
   */
  public static Map<Variable, String> gatherVariableAliases(Iterable<Import> imports, Type type) {
    final Set<String> forbiddenNames =
        Streams.stream(imports)
            // Take the first component of the alias. Most aliases are not qualified names, but
            // externs might be qualified names and only the first component needs to be considered
            // to avoid top level name clashes.
            .map(anImport -> anImport.getAlias().split("\\\\.")[0])
            .collect(toImmutableSet());

    // Gather variables by MemberDescriptor because clinit() and init() are synthesized at
    // generation from multiple members.
    final Multimap<MemberDescriptor, String> variableAliasesByMember = HashMultimap.create();
    final Map<Variable, String> aliasByVariable = new HashMap<>();

    // Create aliases for variables, making sure that variables declared in the same member do not
    // collide with reserved words, aliases or other variables in the same member.
    type.accept(
        new AbstractVisitor() {
          @Override
          public void exitVariable(Variable variable) {
            if (aliasByVariable.containsKey(variable)) {
              return;
            }
            String variableName = variable.getName();
            if (isNameUnavailable(variableName)) {
              // add suffix "$" to the local variable whose name collides with an import alias,
              // a JavaScript keyword or another variable in the same member.
              variableName = variableName + "$";
              int suffix = 0;
              while (isNameUnavailable(variableName + (++suffix) + "$")) {
                // ensure the alias does not collide with other local variables.
              }
              variableName += suffix + "$";
            }
            aliasByVariable.put(variable, variableName);
            variableAliasesByMember.put(getCurrentMember().getDescriptor(), variableName);
          }

          private boolean isNameUnavailable(String variableName) {
            return forbiddenNames.contains(variableName)
                || !JsProtectedNames.isLegalName(variableName)
                || variableAliasesByMember.containsEntry(
                    getCurrentMember().getDescriptor(), variableName);
          }
        });
    return aliasByVariable;
  }
}
