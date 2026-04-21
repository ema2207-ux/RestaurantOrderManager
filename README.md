Cele 11 tipuri de entități:
Am definit o ierarhie  de clase pentru a acoperi toate aspectele restaurantului:
MenuItem (Clasă abstractă de bază)
FoodItem (Subclasa pentru mâncare, cu isVegan)
DrinkItem (Subclasa pentru băuturi, cu volume)
Table (Gestionarea meselor din sală)
Employee (Staff-ul restaurantului)
Order (Comanda propriu-zisă)
OrderItem (Legătura dintre o comandă și produse)
Bill (Factura finală emisă la plată)
PriceDecorator (Pattern Decorator pentru prețuri extra)
ExtraToppingsDecorator (Implementare concretă pentru decorare)
InvalidOrderException (Obiect custom pentru gestionarea erorilor)
