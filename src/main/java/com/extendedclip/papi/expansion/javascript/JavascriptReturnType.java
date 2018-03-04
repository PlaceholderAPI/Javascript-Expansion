/*
 *
 * Javascript-Expansion
 * Copyright (C) 2018 Ryan McCarthy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package com.extendedclip.papi.expansion.javascript;

public enum JavascriptReturnType {

	BOOLEAN("boolean"), STRING("string");
	
	private String type;
	
	JavascriptReturnType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	public static JavascriptReturnType getType(String type){
		for(JavascriptReturnType e : values()){
			if (e.getType().equalsIgnoreCase(type)) {
				return e;
			}
		}
		return null;
	}
	
}
