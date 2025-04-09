using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;

namespace backend.Pluggins.Utilities
{
    /// <summary>
    /// Provides extension methods for converting collections to string representations
    /// </summary>
    public static class CollectionExtensions
    {
        /// <summary>
        /// Converts a List to a string representation similar to Python's list printing
        /// </summary>
        public static string ToPythonString<T>(this List<T> list)
        {
            return $"[{string.Join(", ", list)}]";
        }

        /// <summary>
        /// Converts a Dictionary to a string representation similar to Python's dict printing
        /// </summary>
        public static string ToPythonString<TKey, TValue>(this Dictionary<TKey, TValue> dict) where TKey : notnull
        {
            if(dict == null){ return "{}"; }
            
            var entries = dict.Select(kvp =>
            {
                // Handle List<int> specifically
                if (kvp.Value is List<int> intList)
                {
                    return $"{kvp.Key}: {intList.ToPythonString()}";
                }
                // Default string conversion
                return $"{kvp.Key}: {kvp.Value}";
            });

            return $"{{{string.Join(", ", entries)}}}";
        }
    }
}