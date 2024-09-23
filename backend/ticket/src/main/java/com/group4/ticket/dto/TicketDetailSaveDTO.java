package com.group4.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDetailSaveDTO {

    @NotBlank(message = "Name is required")
    @NotNull(message = "Name can't be NULL")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Username can only contain letters and spaces")
    private String name;
    
    @NotBlank(message = "Phone is mandatory")
    @NotNull(message = "Phone can't be NULL")
    @Pattern(regexp = "^\\+62\\d{9,13}$", message = "Phone number must start with +62 and contain 9 to 13 digits")
    private String phone;
}
