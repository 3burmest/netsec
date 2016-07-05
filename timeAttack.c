#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>

#pragma clang diagnostic ignored "-Wunused-value"

const char pass[9] = "password";

/*
 * Given function to compare a given password with the "hardcoded" one.
 */
int password_compare(const char *password) {
    int i = 0;
    int lengthPass = sizeof(pass)/sizeof(pass[0]);
    int lengthPassword = sizeof(password)/sizeof(password[0]);
    if(lengthPass != lengthPassword) {
        return 0;
    }
    for(i; i<lengthPass && pass[i] == password[i]; i++);
    return i == lengthPass;
}

/*
 * Calls the password_compare function several times to get a somewhat compareable result.
 * TODO: Still very unreliable.
 */
double checker(const char *password, int loops) {
    int i = 0;
    clock_t begin = clock();
    for(i; i < loops; i++) {
        password_compare(password);
    }
    clock_t end = clock();
    
    return (end - begin);
}

/*
 * This function uses the checker to determine the next correct character of the password. It does so by calling the checker function with all the different possible characters at the first not known position in the password. The longest execution time is thought to be the longest correct start which is therefore the result. There is no more logic for determining if it is correct.
 *
 * @param len: The length of the password that should be checked.
 * @param known: The number of characters of the supplied password that we already know to be correct.
 * @param currPass: Pointer to the char array where the current password is. The same char array will hold the new password after execution.
 *
 * @return The execution time of the password with the longest part we think to be correct.
 */
double supplyPass(int len, int known, char *currPass, int loops) {
    double maxTime = 0;
    
    int i = 32;
    int j = 126;
    int extra[6] = {129, 132, 142, 148, 153, 154};
    for(i; i <= j; i++){
        char pass[len];
        if(known == 0) {
            memset(pass, i, len*sizeof(char));
        } else {
            strcpy(pass, currPass);
            pass[known] = i;
        }
        double x = checker(pass, loops);
        if(x > maxTime){
            maxTime = x;
            strcpy(currPass, pass);
        }
    }
    i = 0;
    for(i; i<6; i++){
        char pass[len];
        if(known == 0) {
            memset(pass, extra[i], len*sizeof(char));
        } else {
            strcpy(pass, currPass);
            pass[known] = i;
        }
        double x = checker(pass, loops);
        if(x > maxTime){
            maxTime = x;
            strcpy(currPass, pass);
        }
    }
    
    return maxTime;
}

/**
 *  Calculates the length of the stored password using the supplyPass function.
 *
 *  @param maxLength The maximum length of password that should be tried.
 *  @param pass The variable to hold the password. Afterwards a password with the correct length will be stored inside.
 *  @param loops The times each password length should be tried.
 *
 *  @return Length of the password
 */
int calcLenth(int maxLength, char *pass, int loops) {
    int i = 2;
    int maxTime = 0;
    int length = 1;
    for(i; i <= maxLength+1; i++) {
        pass = (char *) malloc(i);
        int time = supplyPass(i-1, 0, pass, loops);
        free(pass);
        if(time > maxTime) {
            maxTime = time;
            length = i-1;
        }
    }
    
    pass = (char *) malloc(length+1);
    return length;
}

int main(int argc, char *argv[]) {
    char *pass;
    //pass = (char *) malloc(9);
    
    int loops;
    //sscanf(argv[1], "%d", &loops);
    loops = 1000;
    
    //memset(pass, 'p', 8*sizeof(char));
    int length = calcLenth(8, pass, loops);
    int x = 1;
    for (x; x < length; x++) {
        supplyPass(8, 1, pass, loops);
    }
    printf("String: %s\n", pass);
    free(pass);
    return 0;
}
