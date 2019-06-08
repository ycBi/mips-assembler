add $s0, $0,$0
addi $s0, $0, 48
lw $s1, 0($s0)
sw $s1, 0($s0)
lw $s1, 0($s0)
00000014: addi $s0, $s0, 4
lw $t0, 0($s0)
beq $0, $t0, 00000028
add $s1, $s1, $t0
J 00000014
00000028: sw $s1, 0($s0)
END 00000004
DW 1
DW 2
DW 3
DW 4
DW 5
DW 0
DW 7
DW 8
DW 9
DW 10
DW 0
DW 0
DW 4
DW 8
