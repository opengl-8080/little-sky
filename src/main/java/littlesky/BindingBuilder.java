package littlesky;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.Observable;

import java.util.function.Supplier;

public class BindingBuilder {
    
    private Observable[] observables;
    
    public static BindingBuilder binding(Observable... observables) {
        return new BindingBuilder(observables);
    }
    
    public <T> ObjectBinding<T> computeValue(Supplier<T> supplier) {
        return new ObjectBinding<T>() {
            {
                bind(observables);
            }
            
            @Override
            protected T computeValue() {
                return supplier.get();
            }
        };
    }
    
    private BindingBuilder(Observable[] observables) {
        this.observables = observables;
    }
}
