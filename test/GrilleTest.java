import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GrilleTest {
    private Grille grille;

    @BeforeEach
    void setUp() {
        this.grille = new Grille();
    }

    private void furnishValues_toString() {
        this.grille.getGrille().add( new Case( 0, 0, 4 ) );
        this.grille.getGrille().add( new Case( 1, 2, 4 ) );
    }

    @Test
    void testToString() {
        if ( Parametres.TAILLE == 4 ) {
            this.furnishValues_toString();
            String expected = "[4, 0, 0, 0]\n" +
                              "[0, 0, 0, 0]\n" +
                              "[0, 4, 0, 0]\n" +
                              "[0, 0, 0, 0]";

            assertEquals( expected, "" + this.grille, "The results are not corresponding." );
        }
    }

    private void furnishValues_finish() {
        for ( int i = 0; i < Parametres.TAILLE; i++ ) {
            for ( int j = 0; j < Parametres.TAILLE; j++ ) {
                int value;

                if ( i == j ) {
                    value = 4;
                } else if ( i % 2 == 0 ) {
                    value = j % 2 == 0 ? 4 : 2;
                } else {
                    value = j % 2 == 0 ? 2 : 4;
                }

                Case _case = new Case( i, j, value );
                _case.setGrille( this.grille );

                this.grille.getGrille().add( _case );
            }
        }
    }

    @Test
    void partieFinie_finished() {
        this.furnishValues_finish();

        assertTrue( this.grille.partieFinie(), "The grid is considered as not finished. " + this.grille.toString() );
    }

    @Test
    void partieFinie_notFinished_notFilled() {
        this.furnishValues_finish();
        Case _case = new Case( 0, 0, 2 );
        _case.setGrille( this.grille );
        this.grille.getGrille().remove( _case );

        assertFalse( this.grille.partieFinie(), "The grid is considered as finished. \n" + this.grille.toString() );
    }

    @Test
    void partieFinie_notFinished_hasSameValue() {
        this.furnishValues_finish();

        Case _case = new Case( 0, 0, 2 );
        _case.setGrille( this.grille );
        this.grille.getGrille().remove( _case );
        this.grille.getGrille().add( _case );

        assertFalse( this.grille.partieFinie(), "The grid is considered as finished.  \n" + this.grille.toString() );
    }

    private void fusionStub( Case _case ) {
        int newValue = _case.getValue() * 2;
        _case.setValue( newValue );
    }

    @Test
    void fusion() {
        Case _case = new Case( 0, 0, 2 );
        this.fusionStub( _case );

        assertEquals( 4, _case.getValue() );
    }


    private void furnishValues_getCasesExtremites() {
        Case[] cases = {
                new Case( 0, 0, 4 ),
                new Case( 2, 0, 2 ),
                new Case( 0, 2, 4 ),
                new Case( 1, 2, 4 ),
                new Case( 0, 3, 2 ),
                new Case( 1, 3, 8 ),
                new Case( 3, 3, 8 ),
                };

        for ( Case c : cases ) {
            c.setGrille( this.grille );
        }
        this.grille.getGrille().addAll( Arrays.asList( cases ) );

        String example = "[4, 0, 2, 0]\n" +
                         "[0, 0, 0, 0]\n" +
                         "[4, 4, 0, 0]\n" +
                         "[2, 8, 0, 8]";

        if ( !example.equals( "" + this.grille ) ) {
            throw new IllegalArgumentException( "The example is not properly initialized. \nExpected: \n" +
                                                "[4, 0, 2, 0]\n" +
                                                "[0, 0, 0, 0]\n" +
                                                "[4, 4, 0, 0]\n" +
                                                "[2, 8, 0, 8]\n" +
                                                "Got: \n" + this.grille );
        }
    }

    private Case[] furnishExpected() {
        Case[] cases = {
                new Case( 2, 0, 2 ),
                null,
                new Case( 1, 2, 4 ),
                new Case( 3, 3, 8 ),
                };

        for ( Case c : cases ) {
            if ( c != null )
                c.setGrille( this.grille );
        }

        return cases;
    }

    @Test
    void getCasesExtremites_droite() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.furnishValues_getCasesExtremites();

        assertArrayEquals( this.furnishExpected(), this.grille.getCasesExtremites( Parametres.DROITE ) );
    }
}